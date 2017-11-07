/*-
 * #%L
 * marid-fx
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
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

package org.marid.jfx.icons;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.jetbrains.annotations.PropertyKey;

import java.util.logging.Level;

/**
 * @author Dmitry Ovchinnikov
 */
public interface IconFactory {

	static Node icon(Level level) {
		return icon(level, 16);
	}

	static Node icon(Level level, int size) {
		switch (level.intValue()) {
			case Integer.MAX_VALUE: return icon("D_SELECT_OFF", Color.RED, size);
			case Integer.MIN_VALUE: return icon("D_ARROW_ALL", Color.GREEN, size);
			case 1000: return icon("M_ERROR", Color.RED, size);
			case 900: return icon("F_WARNING", Color.ORANGE, size);
			case 800: return icon("F_INFO_CIRCLE", Color.BLUE, size);
			case 700: return icon("M_CONTROL_POINT", Color.GREEN, size);
			case 500: return icon("D_BATTERY_60", Color.GREEN, size);
			case 400: return icon("D_BATTERY_80", Color.GREEN, size);
			case 300: return icon("D_BATTERY_CHARGING_100", Color.GREEN, size);
			default: return icon("D_BATTERY_UNKNOWN", Color.GRAY, size);
		}
	}

	static Node icon(@PropertyKey(resourceBundle = "fonts.meta") String icon, Paint paint, int size) {
		final Text glyphIcon = FontIcons.glyphIcon(icon, size);
		glyphIcon.setFill(paint);
		return glyphIcon;
	}

	static Node icon(@PropertyKey(resourceBundle = "fonts.meta") String icon, Paint paint) {
		return icon(icon, paint, 16);
	}
}
