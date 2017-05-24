/*
 * Copyright (c) 2017 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.dependant.valuemenu;

import javafx.beans.property.Property;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.marid.IdeDependants;
import org.marid.dependant.valuemenu.BeanMetaInfoProvider.BeansMetaInfo;
import org.marid.ide.common.IdeShapes;
import org.marid.ide.project.ProjectProfile;
import org.marid.spring.xml.*;
import org.marid.util.MethodUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static org.marid.jfx.icons.FontIcons.glyphIcon;
import static org.marid.l10n.L10n.s;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class BeanListActions {

    private final ProjectProfile profile;
    private final IdeDependants dependants;
    private final BeanFile beanFile;
    private final BeanMetaInfoProvider metaInfoProvider;

    @Autowired
    public BeanListActions(ProjectProfile profile,
                           IdeDependants dependants,
                           BeanFile beanFile,
                           BeanMetaInfoProvider metaInfoProvider) {
        this.profile = profile;
        this.dependants = dependants;
        this.beanFile = beanFile;
        this.metaInfoProvider = metaInfoProvider;
    }

    private void setData(Property<DElement> element, Object value, BeansMetaInfo metaInfo, boolean insertRefs) {
        if (value instanceof TypedStringValue) {
            final TypedStringValue typedStringValue = (TypedStringValue) value;
            element.setValue(new DValue(typedStringValue.getValue()));
        } else if (value instanceof BeanDefinitionHolder) {
            final BeanDefinitionHolder holder = (BeanDefinitionHolder) value;
            element.setValue(beanData(holder.getBeanName(), holder.getBeanDefinition(), metaInfo, insertRefs));
        }
    }

    public BeanData beanData(String name, BeanDefinition def, BeansMetaInfo metaInfo, boolean insertRefs) {
        final BeanData beanData = new BeanData();
        beanData.name.set(profile.generateBeanName(name));
        beanData.factoryBean.set(def.getFactoryBeanName());
        beanData.factoryMethod.set(def.getFactoryMethodName());
        beanData.type.set(def.getBeanClassName());
        beanData.lazyInit.set(Boolean.toString(def.isLazyInit()));

        if (def instanceof AbstractBeanDefinition) {
            final AbstractBeanDefinition definition = (AbstractBeanDefinition) def;
            beanData.initMethod.set(definition.getInitMethodName());
            beanData.destroyMethod.set(definition.getDestroyMethodName());
        }

        if (def.getConstructorArgumentValues() != null) {
            for (final ValueHolder holder : def.getConstructorArgumentValues().getGenericArgumentValues()) {
                final BeanArg beanArg = new BeanArg();
                beanArg.name.set(holder.getName());
                beanArg.type.set(holder.getType());
                setData(beanArg.data, holder.getValue(), metaInfo, insertRefs);
                beanData.beanArgs.add(beanArg);

                if (holder.getValue() instanceof RuntimeBeanReference) {
                    final RuntimeBeanReference reference = (RuntimeBeanReference) holder.getValue();
                    final BeanDefinition beanDefinition = metaInfo.getBeanDefinition(reference.getBeanName());
                    beanData.beanArgs.stream()
                            .filter(a -> holder.getName().equals(a.getName()))
                            .findFirst()
                            .ifPresent(a -> {
                                if (insertRefs) {
                                    insertItem(reference.getBeanName(), beanDefinition, metaInfo);
                                    a.data.set(new DRef(reference.getBeanName()));
                                } else {
                                    a.data.set(beanData(reference.getBeanName(), beanDefinition, metaInfo, false));
                                }
                            });
                }
            }
        }

        if (def.getPropertyValues() != null) {
            for (final PropertyValue holder : def.getPropertyValues().getPropertyValueList()) {
                final BeanProp property = new BeanProp();
                property.name.set(holder.getName());
                setData(property.data, holder.getValue(), metaInfo, insertRefs);
                beanData.properties.add(property);

                if (holder.getValue() instanceof RuntimeBeanReference) {
                    final RuntimeBeanReference reference = (RuntimeBeanReference) holder.getValue();
                    final BeanDefinition beanDefinition = metaInfo.getBeanDefinition(reference.getBeanName());
                    beanData.properties.stream()
                            .filter(p -> holder.getName().equals(p.getName()))
                            .findFirst()
                            .ifPresent(p -> {
                                if (insertRefs) {
                                    insertItem(reference.getBeanName(), beanDefinition, metaInfo);
                                    p.data.set(new DRef(reference.getBeanName()));
                                } else {
                                    p.data.set(beanData(reference.getBeanName(), beanDefinition, metaInfo, false));
                                }
                            });
                }
            }
        }

        profile.updateBeanData(beanData);
        return beanData;
    }

    public BeanData insertItem(String name, BeanDefinition def, BeansMetaInfo metaInfo) {
        final BeanData beanData = insertItem(beanData(name, def, metaInfo, true));
        ofNullable(def.getFactoryBeanName())
                .filter(f -> !profile.containsBean(f))
                .ifPresent(f -> ofNullable(metaInfo.getBeanDefinition(f)).ifPresent(d -> insertItem(f, d, metaInfo)));
        return beanData;
    }

    public BeanData insertItem(BeanData beanData) {
        beanFile.beans.add(beanData);
        return beanData;
    }

    private MenuItem item(Method method, BeanData beanData) {
        final MenuItem menuItem = new MenuItem(MethodUtils.methodText(method), glyphIcon("M_MEMORY", 16));
        menuItem.setOnAction(ev -> {
            final BeanData newBeanData = new BeanData();
            newBeanData.name.set(profile.generateBeanName(method.getName()));
            newBeanData.factoryBean.set(beanData.name.get());
            newBeanData.factoryMethod.set(method.getName());
            for (final Parameter parameter : method.getParameters()) {
                final BeanArg arg = new BeanArg();
                arg.name.set(parameter.getName());
                arg.type.set(parameter.getType().getName());
                newBeanData.beanArgs.add(arg);
            }
            beanFile.beans.add(newBeanData);
            profile.updateBeanData(newBeanData);
        });
        return menuItem;
    }

    public Menu related(ResolvableType type, BeanData beanData) {
        final Menu related = new Menu(s("Related"));
        final BeansMetaInfo metaInfo = metaInfoProvider.metaInfo();
        for (final BeanDefinitionHolder holder : metaInfo.beans()) {
            final String name = holder.getBeanName();
            final BeanDefinition definition = holder.getBeanDefinition();
            final String relatedArg = metaInfo.related(definition, type, profile);
            if (relatedArg != null) {
                final MenuItem menuItem = new MenuItem(name, IdeShapes.ref(name, 16));
                menuItem.setOnAction(event -> {
                    final BeanData data = insertItem(name, definition, metaInfo);
                    data.beanArgs.filtered(a -> Objects.equals(a.getName(), relatedArg)).forEach(a -> {
                        final DRef ref = new DRef();
                        ref.setBean(beanData.getName());
                        a.setData(ref);
                    });
                });
                related.getItems().add(menuItem);
            }
        }
        return related;
    }

    public List<MenuItem> factoryItems(ResolvableType resolvableType, BeanData beanData) {
        final Class<?> type = resolvableType.getRawClass();
        final Menu getters = new Menu(s("Getters"));
        final Menu producers = new Menu(s("Producers"));
        final Menu parameterizedProducers = new Menu(s("Parameterized producers"));
        for (final Method method : type.getMethods()) {
            if (method.getReturnType() == void.class || method.getDeclaringClass() == Object.class) {
                continue;
            }
            if (method.getParameterCount() == 0) {
                if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
                    getters.getItems().add(item(method, beanData));
                } else {
                    producers.getItems().add(item(method, beanData));
                }
            } else {
                parameterizedProducers.getItems().add(item(method, beanData));
            }
        }
        return Stream.of(getters, producers, parameterizedProducers, related(resolvableType, beanData))
                .filter(m -> !m.getItems().isEmpty())
                .peek(m -> m.getItems().sort(Comparator.comparing(MenuItem::getText)))
                .collect(Collectors.toList());
    }

    public List<MenuItem> editors(ResolvableType type, BeanData beanData) {
        return Collections.emptyList();
    }
}
