package org.marid.runtime.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
    ElementType.CONSTRUCTOR,
    ElementType.FIELD,
    ElementType.METHOD,
    ElementType.PARAMETER,
    ElementType.PACKAGE,
    ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface Icon {
  String value();
}
