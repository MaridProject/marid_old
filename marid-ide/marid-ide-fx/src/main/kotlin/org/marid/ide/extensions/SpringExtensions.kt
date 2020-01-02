package org.marid.ide.extensions

import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.ObjectProvider

val <T> ObjectFactory<T>.bean: T get() = `object`
val <T> ObjectProvider<T>.obj: T? get() = ifAvailable
operator fun <T> ObjectFactory<T>.invoke(): T = `object`
operator fun <T> ObjectProvider<T>.invoke(vararg args: Any?): T = getObject(*args)