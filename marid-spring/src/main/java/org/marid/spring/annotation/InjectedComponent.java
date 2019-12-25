package org.marid.spring.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
@Inherited
@Component
public @interface InjectedComponent {

  @AliasFor(annotation = Component.class, attribute = "value")
  String value() default "";
}
