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

import javafx.event.Event
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.input.SwipeEvent
import javafx.scene.input.TouchEvent
import java.awt.event.KeyEvent

val Event.isAltered
  get() = when (this) {
    is KeyEvent -> isAltDown || isAltGraphDown || isControlDown || isMetaDown || isShiftDown
    is MouseEvent -> isAltDown || isControlDown || isMetaDown || isShiftDown
    is ScrollEvent -> isAltDown || isControlDown || isMetaDown || isShiftDown
    is TouchEvent -> isAltDown || isControlDown || isMetaDown || isShiftDown
    is SwipeEvent -> isAltDown || isControlDown || isMetaDown || isShiftDown
    else -> false
  }
