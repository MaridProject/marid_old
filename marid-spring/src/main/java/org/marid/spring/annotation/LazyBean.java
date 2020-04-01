package org.marid.spring.annotation;

/*-
 * #%L
 * marid-spring
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
