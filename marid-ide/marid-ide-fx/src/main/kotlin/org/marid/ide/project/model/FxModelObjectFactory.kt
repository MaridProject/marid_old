/*-
 * #%L
 * marid-ide-fx
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

package org.marid.ide.project.model

import org.marid.model.Initializer
import org.marid.model.Literal
import org.marid.model.ModelObjectFactory

object FxModelObjectFactory : ModelObjectFactory {
  override fun newConstRef() = FxConstRef()
  override fun newCellarConstant() = FxCellarConstant()
  override fun newNull() = FxNull
  override fun newRack() = FxRack()
  override fun newWinery() = FxWinery()
  override fun newCellar() = FxCellar()
  override fun newRef() = FxRef()
  override fun newOutput() = FxOutput()
  override fun newInitializer(): Initializer = FxInitializer()
  override fun newLiteral(): Literal = FxLiteral()
}
