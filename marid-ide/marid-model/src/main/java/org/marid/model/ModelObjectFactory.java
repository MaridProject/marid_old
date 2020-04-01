package org.marid.model;

/*-
 * #%L
 * marid-model
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

public interface ModelObjectFactory {

  ModelObjectFactoryImpl FACTORY = new ModelObjectFactoryImpl();

  Winery newWinery();

  Ref newRef();

  Rack newRack();

  Output newOutput();

  Null newNull();

  Literal newLiteral();

  ConstRef newConstRef();

  Cellar newCellar();

  Initializer newInitializer();

  CellarConstant newCellarConstant();

  default Entity newEntity(String tag) {
    return ModelObjectFactoryFriend.newEntity(this, tag);
  }
}
