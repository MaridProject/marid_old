package org.marid.ide.extensions

import org.springframework.beans.factory.*
import org.springframework.core.ResolvableType
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

private typealias P<T> = ObjectProvider<T>

val <T> ObjectFactory<T>.bean: T get() = `object`
val <T> ObjectProvider<T>.obj: T? get() = ifAvailable
operator fun <T> ObjectFactory<T>.invoke(): T = `object`
operator fun <T> ObjectProvider<T>.invoke(vararg args: Any?): T = getObject(*args)

inline fun <reified T> BeanFactory.bean(): T = getBean(T::class.java)
inline fun <reified T> BeanFactory.bean(name: String): T = getBean(name, T::class.java)
inline fun <reified T> BeanFactory.beans(): List<T> = getBeanProvider(T::class.java).toList()

@ExperimentalStdlibApi inline fun <reified T> BeanFactory.provider(): P<T> {
  val type = typeOf<T>()
  val resolvableType = ResolvableType.forType(type.javaType)
  return getBeanProvider<T>(resolvableType)
}

@ExperimentalStdlibApi inline fun <reified T> ListableBeanFactory.genericBean(): T {
  val type = typeOf<T>()
  val resolvableType = ResolvableType.forType(type.javaType)
  return getBeanProvider<T>(resolvableType).getObject()
}

@ExperimentalStdlibApi inline fun <reified T> ListableBeanFactory.genericBean(name: String): T {
  val type = typeOf<T>()
  val resolvableType = ResolvableType.forType(type.javaType)
  val names = getBeanNamesForType(resolvableType)
  if (name in names) {
    return getBean(T::class.java)
  } else {
    throw NoSuchBeanDefinitionException(name)
  }
}