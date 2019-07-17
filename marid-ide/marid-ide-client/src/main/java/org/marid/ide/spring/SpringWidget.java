package org.marid.ide.spring;

import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.core.ResolvableType;

public class SpringWidget<T> implements SmartFactoryBean<T> {

  protected final T widget;

  public SpringWidget(T widget) {
    this.widget = widget;
  }

  @Override
  public final T getObject() {
    return widget;
  }

  @Override
  public final Class<?> getObjectType() {
    final var type = ResolvableType.forClass(SpringWidget.class, getClass());
    return type.getGeneric(0).getRawClass();
  }

  @Override
  public final boolean isSingleton() {
    return true;
  }

  @Override
  public final boolean isEagerInit() {
    return true;
  }

  @Override
  public final int hashCode() {
    return widget.hashCode();
  }

  @Override
  public final boolean equals(Object obj) {
    return obj == this || obj instanceof SpringWidget && ((SpringWidget) obj).widget.equals(widget);
  }
}
