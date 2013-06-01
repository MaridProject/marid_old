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

package org.marid.ide;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Dmitry Ovchinnikov
 */
public class TestMaridIde {

    public static void main(String... args) throws Exception {
        final File u = new File(System.getProperty("user.dir"));
        final URLClassLoader cl = new URLClassLoader(new URL[] {
                new File(u, "marid-ide/src/ext").toURI().toURL(),
                new File(u, "marid-ide/src/groovyExt").toURI().toURL()
        });
        Thread.currentThread().setContextClassLoader(cl);
        MaridIde.main(args);
    }
}
