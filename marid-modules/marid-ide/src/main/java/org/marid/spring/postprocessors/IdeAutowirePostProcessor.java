package org.marid.spring.postprocessors;

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

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.logging.Level.WARNING;
import static org.marid.logging.Log.log;
import static org.springframework.context.annotation.AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME;

public class IdeAutowirePostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements MergedBeanDefinitionPostProcessor {

    private final ConfigurableListableBeanFactory beanFactory;
	private final List<Class<? extends Annotation>> autowiredAnnotationTypes = asList(Autowired.class, Value.class);
	private final Map<Class<?>, Constructor<?>[]> candidateConstructorsCache = new ConcurrentHashMap<>(256);
	private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);

	public IdeAutowirePostProcessor(ConfigurableListableBeanFactory beanFactory) {
	    this.beanFactory = beanFactory;
    }

	public static void register(DefaultListableBeanFactory beanFactory) {
	    final RootBeanDefinition definition = new RootBeanDefinition(IdeAutowirePostProcessor.class);
	    definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
	    definition.getConstructorArgumentValues().addGenericArgumentValue(beanFactory);
	    beanFactory.registerBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME, definition);
    }

	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		if (beanType != null) {
			final InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
			metadata.checkConfigMembers(beanDefinition);
		}
	}

	@Override
	public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeanCreationException {
        return candidateConstructorsCache.computeIfAbsent(beanClass, c -> {
            final Constructor<?>[] rawCandidates = beanClass.getDeclaredConstructors();
            final ArrayList<Constructor<?>> candidates = new ArrayList<>(rawCandidates.length);
            Constructor<?> requiredConstructor = null;
            Constructor<?> defaultConstructor = null;
            for (Constructor<?> candidate : rawCandidates) {
                AnnotationAttributes ann = findAutowiredAnnotation(candidate);
                if (ann == null) {
                    Class<?> userClass = ClassUtils.getUserClass(beanClass);
                    if (userClass != beanClass) {
                        try {
                            Constructor<?> superCtor =
                                    userClass.getDeclaredConstructor(candidate.getParameterTypes());
                            ann = findAutowiredAnnotation(superCtor);
                        } catch (NoSuchMethodException ex) {
                            // Simply proceed, no equivalent superclass constructor found...
                        }
                    }
                }
                if (ann != null) {
                    if (requiredConstructor != null) {
                        throw new BeanCreationException(beanName,
                                "Invalid autowire-marked constructor: " + candidate +
                                        ". Found constructor with 'required' Autowired annotation already: " +
                                        requiredConstructor);
                    }
                    boolean required = (!ann.containsKey("required") || ann.getBoolean("required"));
                    if (required) {
                        if (!candidates.isEmpty()) {
                            throw new BeanCreationException(beanName,
                                    "Invalid autowire-marked constructors: " + candidates +
                                            ". Found constructor with 'required' Autowired annotation: " +
                                            candidate);
                        }
                        requiredConstructor = candidate;
                    }
                    candidates.add(candidate);
                } else if (candidate.getParameterTypes().length == 0) {
                    defaultConstructor = candidate;
                }
            }
            if (!candidates.isEmpty()) {
                if (requiredConstructor == null) {
                    if (defaultConstructor != null) {
                        candidates.add(defaultConstructor);
                    } else if (candidates.size() == 1) {
                        throw new BeanCreationException(beanName, "Invalid constructor " + candidates.get(0));
                    }
                }
                return candidates.toArray(new Constructor<?>[candidates.size()]);
            } else if (rawCandidates.length == 1 && rawCandidates[0].getParameterTypes().length > 0) {
                return new Constructor<?>[] {rawCandidates[0]};
            } else {
                return null;
            }
        });
	}

	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs,
													PropertyDescriptor[] pds,
													Object bean,
													String beanName) throws BeanCreationException {

		final InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
		try {
			metadata.inject(bean, beanName, pvs);
		} catch (BeanCreationException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
		}
		return pvs;
	}

	private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
		return injectionMetadataCache.compute(StringUtils.hasLength(beanName) ? beanName : clazz.getName(), (k, m) -> {
            if (InjectionMetadata.needsRefresh(m, clazz)) {
                if (m != null) {
                    m.clear(pvs);
                }
                try {
                    m = buildAutowiringMetadata(clazz);
                } catch (NoClassDefFoundError err) {
                    throw new IllegalStateException("Failed to introspect bean class [" + clazz.getName() +
                            "] for autowiring metadata: could not find class that it depends on", err);
                }
            }
            return m;
        });
	}

	private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {
		final List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
		Class<?> targetClass = clazz;

		do {
			final LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<>();

			ReflectionUtils.doWithLocalFields(targetClass, field -> {
                AnnotationAttributes ann = findAutowiredAnnotation(field);
                if (ann != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        log(WARNING, "Autowired annotation is not supported on static fields: {0}", field);
                        return;
                    }
                    boolean required = (!ann.containsKey("required") || ann.getBoolean("required"));
                    currElements.add(new AutowiredFieldElement(field, required));
                }
            });

			ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    return;
                }
                AnnotationAttributes ann = findAutowiredAnnotation(bridgedMethod);
                if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        return;
                    }
                    boolean required = (!ann.containsKey("required") || ann.getBoolean("required"));
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                    currElements.add(new AutowiredMethodElement(method, required, pd));
                }
            });

			elements.addAll(0, currElements);
			targetClass = targetClass.getSuperclass();
		} while (targetClass != null && targetClass != Object.class);


        elements.sort((ie1, ie2) -> {
            if (ie1.getMember() instanceof AnnotatedElement && ie2.getMember() instanceof AnnotatedElement) {
                final AnnotatedElement e1 = (AnnotatedElement) ie1.getMember();
                final AnnotatedElement e2 = (AnnotatedElement) ie2.getMember();
                final Order o1 = e1.getAnnotation(Order.class);
                final Order o2 = e2.getAnnotation(Order.class);
                if (o1 != null && o2 != null) {
                    return Integer.compare(o1.value(), o2.value());
                } else if (o1 == null && o2 == null) {
                    return ie1.getMember().getName().compareTo(ie2.getMember().getName());
                } else {
                    final int i1 = o1 != null ? o1.value() : Ordered.LOWEST_PRECEDENCE;
                    final int i2 = o2 != null ? o2.value() : Ordered.LOWEST_PRECEDENCE;
                    return Integer.compare(i1, i2);
                }
            } else if (ie1.getMember() instanceof AnnotatedElement) {
                return -1;
            } else if (ie2.getMember() instanceof AnnotatedElement) {
                return 1;
            } else {
                return ie1.getMember().getName().compareTo(ie2.getMember().getName());
            }
        });

		return new InjectionMetadata(clazz, elements);
	}

	private AnnotationAttributes findAutowiredAnnotation(AccessibleObject ao) {
	    if (ao.getAnnotations().length > 0) {
			for (Class<? extends Annotation> type : autowiredAnnotationTypes) {
				AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ao, type);
				if (attributes != null) {
					return attributes;
				}
			}
		}
		return null;
	}

    private void registerDependentBeans(String beanName, Set<String> autowiredBeanNames) {
		if (beanName != null) {
			for (String autowiredBeanName : autowiredBeanNames) {
				if (beanFactory.containsBean(autowiredBeanName)) {
					beanFactory.registerDependentBean(autowiredBeanName, beanName);
				}
			}
		}
	}

	private Object resolvedCachedArgument(String beanName, Object cachedArgument) {
		if (cachedArgument instanceof DependencyDescriptor) {
			final DependencyDescriptor descriptor = (DependencyDescriptor) cachedArgument;
			return beanFactory.resolveDependency(descriptor, beanName, null, null);
		} else {
			return cachedArgument;
		}
	}

    private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {

		private final boolean required;
		private volatile boolean cached = false;
		private volatile Object cachedFieldValue;

		AutowiredFieldElement(Field field, boolean required) {
			super(field, null);
			this.required = required;
		}

		@Override
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
			Field field = (Field) this.member;
			Object value;
			if (this.cached) {
				value = resolvedCachedArgument(beanName, this.cachedFieldValue);
			} else {
				DependencyDescriptor desc = new DependencyDescriptor(field, this.required);
				desc.setContainingClass(bean.getClass());
				Set<String> autowiredBeanNames = new LinkedHashSet<>(1);
				TypeConverter typeConverter = beanFactory.getTypeConverter();
				try {
					value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
				} catch (BeansException ex) {
					throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
				}
				synchronized (this) {
					if (!this.cached) {
						if (value != null || this.required) {
							this.cachedFieldValue = desc;
							registerDependentBeans(beanName, autowiredBeanNames);
							if (autowiredBeanNames.size() == 1) {
								String autowiredBeanName = autowiredBeanNames.iterator().next();
								if (beanFactory.containsBean(autowiredBeanName)) {
									if (beanFactory.isTypeMatch(autowiredBeanName, field.getType())) {
										this.cachedFieldValue = new ShortcutDependencyDescriptor(
												desc, autowiredBeanName, field.getType());
									}
								}
							}
						} else {
							this.cachedFieldValue = null;
						}
						this.cached = true;
					}
				}
			}
			if (value != null) {
				ReflectionUtils.makeAccessible(field);
				field.set(bean, value);
			}
		}
	}


	private class AutowiredMethodElement extends InjectionMetadata.InjectedElement {

		private final boolean required;
		private volatile boolean cached = false;
		private volatile Object[] cachedMethodArguments;

		AutowiredMethodElement(Method method, boolean required, PropertyDescriptor pd) {
			super(method, pd);
			this.required = required;
		}

		@Override
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
			if (checkPropertySkipping(pvs)) {
				return;
			}
			Method method = (Method) this.member;
			Object[] arguments;
			if (this.cached) {
				// Shortcut for avoiding synchronization...
				arguments = resolveCachedArguments(beanName);
			} else {
				Class<?>[] paramTypes = method.getParameterTypes();
				arguments = new Object[paramTypes.length];
				DependencyDescriptor[] descriptors = new DependencyDescriptor[paramTypes.length];
				Set<String> autowiredBeans = new LinkedHashSet<>(paramTypes.length);
				TypeConverter typeConverter = beanFactory.getTypeConverter();
				for (int i = 0; i < arguments.length; i++) {
					MethodParameter methodParam = new MethodParameter(method, i);
					DependencyDescriptor currDesc = new DependencyDescriptor(methodParam, this.required);
					currDesc.setContainingClass(bean.getClass());
					descriptors[i] = currDesc;
					try {
						Object arg = beanFactory.resolveDependency(currDesc, beanName, autowiredBeans, typeConverter);
						if (arg == null && !this.required) {
							arguments = null;
							break;
						}
						arguments[i] = arg;
					} catch (BeansException ex) {
						throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(methodParam), ex);
					}
				}
				synchronized (this) {
					if (!this.cached) {
						if (arguments != null) {
							this.cachedMethodArguments = new Object[paramTypes.length];
                            System.arraycopy(descriptors, 0, this.cachedMethodArguments, 0, arguments.length);
							registerDependentBeans(beanName, autowiredBeans);
							if (autowiredBeans.size() == paramTypes.length) {
								Iterator<String> it = autowiredBeans.iterator();
								for (int i = 0; i < paramTypes.length; i++) {
									String autowiredBeanName = it.next();
									if (beanFactory.containsBean(autowiredBeanName)) {
										if (beanFactory.isTypeMatch(autowiredBeanName, paramTypes[i])) {
											this.cachedMethodArguments[i] = new ShortcutDependencyDescriptor(
													descriptors[i], autowiredBeanName, paramTypes[i]);
										}
									}
								}
							}
						} else {
							this.cachedMethodArguments = null;
						}
						this.cached = true;
					}
				}
			}
			if (arguments != null) {
				try {
					ReflectionUtils.makeAccessible(method);
					method.invoke(bean, arguments);
				}
				catch (InvocationTargetException ex){
					throw ex.getTargetException();
				}
			}
		}

		private Object[] resolveCachedArguments(String beanName) {
		    return Optional.ofNullable(cachedMethodArguments)
                    .map(a -> Stream.of(a).map(e -> resolvedCachedArgument(beanName, e)).toArray()).orElse(null);
		}
	}

	private static class ShortcutDependencyDescriptor extends DependencyDescriptor {

		private final String shortcut;
		private final Class<?> requiredType;

		ShortcutDependencyDescriptor(DependencyDescriptor original, String shortcut, Class<?> requiredType) {
			super(original);
			this.shortcut = shortcut;
			this.requiredType = requiredType;
		}

		@Override
		public Object resolveShortcut(BeanFactory beanFactory) {
			return resolveCandidate(this.shortcut, this.requiredType, beanFactory);
		}
	}
}
