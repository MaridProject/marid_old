/*
 * Copyright (c) 2016 Dmitry Ovchinnikov
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

package org.marid.editors.hmi.screen;

import org.marid.spring.xml.BeanData;
import org.marid.spring.xml.BeanProp;
import org.marid.spring.xml.collection.DElement;

/**
 * @author Dmitry Ovchinnikov
 */
public interface DemoUtils {

    static BeanProp prop(BeanData beanData, String name, Class<?> type, DElement<?> value) {
        return beanData.property(name)
                .map(p -> {
                    p.type.set(type.getName());
                    p.data.set(value);
                    return p;
                })
                .orElseGet(() -> {
                    final BeanProp prop = new BeanProp();
                    prop.data.set(value);
                    prop.name.set(name);
                    prop.type.set(type.getName());
                    beanData.properties.add(prop);
                    return prop;
                });
    }
}