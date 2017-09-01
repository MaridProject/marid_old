/*-
 * #%L
 * marid-ide
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.marid.ide.types;

import com.google.common.reflect.TypeResolver;
import com.google.common.reflect.TypeToken;
import org.marid.ide.model.BeanData;
import org.marid.ide.model.BeanMethodArgData;
import org.marid.misc.Casts;
import org.marid.runtime.exception.MaridBeanNotFoundException;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;

import static com.google.common.reflect.TypeToken.of;
import static org.marid.runtime.context.MaridRuntimeUtils.fromSignature;
import static org.marid.runtime.context.MaridRuntimeUtils.initializer;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class BeanTypeResolver {

    public BeanTypeInfo resolve(BeanContext context, BeanData base, String name) {
        return context.getBean(base, name)
                .map(b -> resolve(context, b))
                .orElseGet(() -> new EmptyBeanTypeInfo(new MaridBeanNotFoundException(name)));
    }

    public BeanTypeInfo resolve(BeanContext context, BeanData beanData) {
        try {
            return resolveUnsafe(context, beanData);
        } catch (Exception x) {
            return new EmptyBeanTypeInfo(x);
        }
    }

    public BeanTypeInfo resolveUnsafe(BeanContext context, BeanData beanData) throws Exception {
        final BeanFactoryInfo info = new BeanFactoryInfo(beanData, this, context);
        final Map<TypeToken<?>, List<TypeToken<?>>> pairs = new LinkedHashMap<>();
        final Type[] beanPs = formalTypes(info.returnHandle, true);
        final Type[] beanAs = new Type[beanPs.length];
        for (int k = 0; k < beanAs.length; k++) {
            beanAs[k] = actualType(context, beanData.args.get(k), beanPs[k]);
        }
        for (int i = 0; i < beanPs.length; i++) {
            if (beanAs[i] != null) {
                resolve(pairs, info.factoryToken.resolveType(beanPs[i]), of(beanAs[i]));
            }
        }
        final Type[][] initPs = new Type[info.bean.initializers.size()][];
        final Type[][] initAs = new Type[info.bean.initializers.size()][];
        for (int i = 0; i < info.bean.initializers.size(); i++) {
            final String signature = info.bean.initializers.get(i).signature;
            final MethodHandle handle = initializer(fromSignature(signature, context.getClassLoader()));
            final Type[] ps = formalTypes(handle, false);
            final Type[] as = new Type[ps.length];
            for (int k = 0; k < ps.length; k++) {
                as[k] = actualType(context, beanData.getArgs(i).get(k), ps[k]);
            }
            for (int k = 0; k < as.length; k++) {
                if (as[k] != null) {
                    resolve(pairs, info.factoryToken.resolveType(ps[k]), of(as[k]));
                }
            }
            initPs[i] = ps;
            initAs[i] = as;
        }

        final TypeResolver r = pairs.entrySet().stream().reduce(
                new TypeResolver(),
                (a, e) -> a.where(e.getKey().getType(), commonAncestor(e).getType()),
                (r1, r2) -> r2
        );
        final UnaryOperator<Type> resolverFunc = t -> info.factoryToken.resolveType(r.resolveType(t)).getType();
        return new GenericBeanTypeInfo(resolverFunc, info.returnType, beanPs, beanPs, initPs, initAs);
    }

    private TypeToken<?> commonAncestor(Entry<TypeToken<?>, List<TypeToken<?>>> entry) {
        final Optional<? extends TypeToken<?>> token = entry.getValue().stream()
                .flatMap(t -> t.getTypes().stream())
                .filter(c -> entry.getValue().stream().allMatch(t -> t.isSubtypeOf(c)))
                .findFirst();
        return token.isPresent() ? token.get() : entry.getKey();
    }

    private Type[] formalTypes(MethodHandle handle, boolean getters) throws IllegalAccessException {
        final Member m = MethodHandles.reflectAs(Member.class, handle);
        return m instanceof Field
                ? (getters ? new Type[0] : new Type[]{((Field) m).getGenericType()})
                : ((Executable) m).getGenericParameterTypes();
    }

    private Type actualType(BeanContext context, BeanMethodArgData arg, Type formalType) {
        switch (arg.getType()) {
            case "ref":
                return resolve(context, arg.parent.parent, arg.getValue()).getType();
            case "of":
                if (TypeToken.of(formalType).getRawType() == Class.class && arg.getValue() != null) {
                    return TypeUtilities.classType(context.getClassLoader(), arg.getValue());
                }
            default: {
                final Type t = context.getConverters().getType(arg.getType()).orElse(null);
                return t instanceof WildcardType ? formalType : t;
            }
        }
    }

    private void resolve(Map<TypeToken<?>, List<TypeToken<?>>> map, TypeToken<?> formal, TypeToken<?> actual) {
        resolve(new HashSet<>(), map, formal, actual);
    }

    private void resolve(Set<TypeToken<?>> passed, Map<TypeToken<?>, List<TypeToken<?>>> map, TypeToken<?> formal, TypeToken<?> actual) {
        if (!passed.add(formal)) {
            return;
        }
        if (formal.isArray() && actual.isArray()) {
            resolve(passed, map, formal.getComponentType(), actual.getComponentType());
        } else if (formal.getType() instanceof TypeVariable<?>) {
            final TypeVariable<?> typeVariable = (TypeVariable<?>) formal.getType();
            for (final Type bound : typeVariable.getBounds()) {
                resolve(passed, map, of(bound), actual);
            }
            map.computeIfAbsent(formal, k -> new ArrayList<>()).add(actual.wrap());
        } else if (formal.getType() instanceof ParameterizedType) {
            final Class<?> formalRaw = formal.getRawType();
            final Class<?> actualRaw = actual.getRawType();
            if (formalRaw.isAssignableFrom(actualRaw)) {
                final TypeToken<?> superType = actual.getSupertype(Casts.cast(formalRaw));
                final ParameterizedType actualParameterized = (ParameterizedType) superType.getType();
                final ParameterizedType formalParameterized = (ParameterizedType) formal.getType();
                final Type[] actualTypeArgs = actualParameterized.getActualTypeArguments();
                final Type[] formalTypeArgs = formalParameterized.getActualTypeArguments();
                for (int i = 0; i < actualTypeArgs.length; i++) {
                    resolve(passed, map, of(formalTypeArgs[i]), of(actualTypeArgs[i]));
                }
            }
        } else if (formal.getType() instanceof WildcardType) {
            final WildcardType wildcardType = (WildcardType) formal.getType();
            for (final Type bound : wildcardType.getUpperBounds()) {
                resolve(passed, map, of(bound), actual);
            }
        }
    }
}
