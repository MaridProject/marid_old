/*
 * Copyright (C) 2013 Dmitry Ovchinnikov
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

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Dmitry Ovchinnikov
 */
public abstract class JaxbObject implements Serializable {

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        } else {
            try {
                final ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
                try (final ObjectOutputStream oos1 = new ObjectOutputStream(bos1)) {
                    oos1.writeObject(this);
                }
                final ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
                try (final ObjectOutputStream oos2 = new ObjectOutputStream(bos2)) {
                    oos2.writeObject(obj);
                }
                return Arrays.equals(bos1.toByteArray(), bos2.toByteArray());
            } catch (Exception x) {
                throw new IllegalStateException(x);
            }
        }
    }

    @Override
    public int hashCode() {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (final ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(this);
            }
            return Arrays.hashCode(bos.toByteArray());
        } catch (Exception x) {
            throw new IllegalStateException(x);
        }
    }
}
