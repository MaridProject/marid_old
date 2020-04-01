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

@file:Suppress("UnstableApiUsage")

package org.marid.fx.extensions

import com.google.common.io.MoreFiles
import com.google.common.io.RecursiveDeleteOption
import java.nio.file.Path

fun Path.deleteDirectoryContents(vararg options: RecursiveDeleteOption) = MoreFiles.deleteDirectoryContents(this, *options)
fun Path.deleteDirectory(vararg options: RecursiveDeleteOption) = MoreFiles.deleteRecursively(this, *options)
