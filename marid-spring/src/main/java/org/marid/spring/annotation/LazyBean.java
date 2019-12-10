package org.marid.spring.annotation;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Lazy
@Bean
public @interface LazyBean {

  @AliasFor(annotation = Bean.class, attribute = "value")
  String[] value() default {};

  @AliasFor(annotation = Bean.class, attribute = "name")
  String[] name() default {};

  @AliasFor(annotation = Bean.class, attribute = "autowireCandidate")
  boolean autowireCandidate() default true;

  @AliasFor(annotation = Bean.class, attribute = "initMethod")
  String initMethod() default "";

  @AliasFor(annotation = Bean.class, attribute = "destroyMethod")
  String destroyMethod() default AbstractBeanDefinition.INFER_METHOD;
}
