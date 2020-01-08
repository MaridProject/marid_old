package org.marid.fx.xml

import org.w3c.dom.Element
import org.w3c.dom.Node

operator fun Element.get(key: String): String = getAttribute(key)
operator fun Element.set(key: String, value: String) = setAttribute(key, value)

fun Node.newChild(tag: String): Element = ownerDocument.createElement(tag)
fun Node.child(tag: String): Element = newChild(tag).also { appendChild(it) }