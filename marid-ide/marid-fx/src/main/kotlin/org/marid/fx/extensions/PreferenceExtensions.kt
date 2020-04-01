/*-
 * #%L
 * marid-fx
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

package org.marid.fx.extensions

import javafx.application.Platform
import javafx.beans.property.*
import java.util.*
import java.util.prefs.Preferences

inline fun <reified T> T.pref(name: String, default: String): StringProperty {
  val node = Preferences.userNodeForPackage(T::class.java).node(T::class.simpleName)
  val prop = SimpleStringProperty(this, name, node.get(name, default))
  node.addPreferenceChangeListener { e ->
    if (e.key == name) {
      Platform.runLater { prop.set(e.newValue) }
    }
  }
  prop.addListener { _, _, n -> node.put(name, n) }
  return prop
}

inline fun <reified T> T.pref(name: String, default: Int): IntegerProperty {
  val node = Preferences.userNodeForPackage(T::class.java).node(T::class.simpleName)
  val prop = SimpleIntegerProperty(this, name, node.getInt(name, default))
  node.addPreferenceChangeListener { e ->
    if (e.key == name) {
      Platform.runLater { prop.set(node.getInt(name, default)) }
    }
  }
  prop.addListener { _, _, n -> node.putInt(name, n.toInt()) }
  return prop
}

inline fun <reified T> T.pref(name: String, default: Double): DoubleProperty {
  val node = Preferences.userNodeForPackage(T::class.java).node(T::class.simpleName)
  val prop = SimpleDoubleProperty(this, name, node.getDouble(name, default))
  node.addPreferenceChangeListener { e ->
    if (e.key == name) {
      Platform.runLater { prop.set(node.getDouble(name, default)) }
    }
  }
  prop.addListener { _, _, n -> node.putDouble(name, n.toDouble()) }
  return prop
}

inline fun <reified T> T.pref(name: String, default: Float): FloatProperty {
  val node = Preferences.userNodeForPackage(T::class.java).node(T::class.simpleName)
  val prop = SimpleFloatProperty(this, name, node.getFloat(name, default))
  node.addPreferenceChangeListener { e ->
    if (e.key == name) {
      Platform.runLater { prop.set(node.getFloat(name, default)) }
    }
  }
  prop.addListener { _, _, n -> node.putFloat(name, n.toFloat()) }
  return prop
}

inline fun <reified T> T.pref(name: String, default: Boolean): BooleanProperty {
  val node = Preferences.userNodeForPackage(T::class.java).node(T::class.simpleName)
  val prop = SimpleBooleanProperty(this, name, node.getBoolean(name, default))
  node.addPreferenceChangeListener { e ->
    if (e.key == name) {
      Platform.runLater { prop.set(node.getBoolean(name, default)) }
    }
  }
  prop.addListener { _, _, n -> node.putBoolean(name, n) }
  return prop
}

inline fun <reified T> T.pref(name: String, default: ByteArray): ObjectProperty<ByteArray> {
  val node = Preferences.userNodeForPackage(T::class.java).node(T::class.simpleName)
  val prop = SimpleObjectProperty(this, name, node.getByteArray(name, default))
  node.addPreferenceChangeListener { e ->
    if (e.key == name) {
      Platform.runLater { prop.set(node.getByteArray(name, default)) }
    }
  }
  prop.addListener { _, _, n -> node.putByteArray(name, n) }
  return prop
}

inline fun <reified T> T.pref(name: String, default: Locale): ObjectProperty<Locale> {
  val node = Preferences.userNodeForPackage(T::class.java).node(T::class.simpleName)
  val prop = SimpleObjectProperty(this, name, Locale.forLanguageTag(node.get(name, default.toLanguageTag())))
  node.addPreferenceChangeListener { e ->
    if (e.key == name) {
      Platform.runLater { prop.set(Locale.forLanguageTag(e.newValue)) }
    }
  }
  prop.addListener { _, _, n -> node.put(name, n.toLanguageTag()) }
  return prop
}

inline fun <reified T, reified E : Enum<E>> T.pref(name: String, default: E): ObjectProperty<E> {
  val node = Preferences.userNodeForPackage(T::class.java).node(T::class.simpleName)
  val prop = SimpleObjectProperty(this, name, enumValueOf<E>(node.get(name, default.name)))
  node.addPreferenceChangeListener { e ->
    if (e.key == name) {
      Platform.runLater { prop.set(enumValueOf(e.newValue)) }
    }
  }
  prop.addListener { _, _, n -> node.put(name, n.name) }
  return prop
}
