package org.marid.ide.project.xml

import javafx.collections.FXCollections
import org.marid.xml.XmlStreams
import org.w3c.dom.Element
import kotlin.streams.toList

class XmlEntries(vararg entries: XmlEntry) {

  val items = FXCollections.observableArrayList(XmlEntry::observables)
  val observables = arrayOf(items)

  init {
    items.setAll(*entries)
  }

  constructor(e: Element) : this(*XmlStreams.elementsByTag(e, "entry").map(::XmlEntry).toList().toTypedArray())
}