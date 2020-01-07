package org.marid.fx.xml

import org.marid.xml.XmlStreams
import org.w3c.dom.Element
import org.w3c.dom.Node
import kotlin.streams.asSequence

operator fun Element.get(key: String): String = getAttribute(key)
operator fun Element.set(key: String, value: Any) = setAttribute(key, value.toString())

val Element.elements: Sequence<Element> get() = XmlStreams.children(this, Element::class.java).asSequence()
val Element.nodes: Sequence<Node> get() = XmlStreams.children(this).asSequence()
fun Element.newChild(tag: String): Element = ownerDocument.createElement(tag)
fun Element.child(tag: String): Element = newChild(tag).also { appendChild(it) }