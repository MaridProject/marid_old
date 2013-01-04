/*
 * Copyright (C) 2012 Dmitry Ovchinnikov
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
package org.marid.util;

import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;

/**
 * Abstract propertized object class.
 *
 * @author Dmitry Ovchinnikov (d.ovchinnikow at gmail.com)
 */
public abstract class AbstractPrp implements Prp {

    @Override
    public Object get(String key, Object def) {
        Object v = get(key);
        return v == null ? def : v;
    }

    @Override
    public <T> T get(Class<T> c, String key, T def) {
        Object v = get(key, def);
        if (v == null) {
            return null;
        } else {
            Class<?> cl = v.getClass();
            MetaClass mc = GroovySystem.getMetaClassRegistry().getMetaClass(cl);
            return c.cast(mc.invokeMethod(v, "asType", c));
        }
    }
}
