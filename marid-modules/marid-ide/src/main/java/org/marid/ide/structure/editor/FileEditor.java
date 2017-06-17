/*
 * Copyright (c) 2017 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.ide.structure.editor;

import org.jetbrains.annotations.PropertyKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;

/**
 * @author Dmitry Ovchinnikov
 */
public interface FileEditor {

    @Nullable
    Runnable getEditAction(@Nonnull Path path);

    @Nonnull
    String getName();

    @Nonnull
    String getIcon();

    @Nonnull
    String getGroup();

    @Nullable
    default String getSpecialAction() {
        return null;
    }

    default String icon(@PropertyKey(resourceBundle = "fonts.meta") String icon) {
        return icon;
    }
}
