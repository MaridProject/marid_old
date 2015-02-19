/*
 * Copyright (C) 2014 Dmitry Ovchinnikov
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

package org.marid.groovy;

import groovy.transform.Field;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;

/**
 * @author Dmitry Ovchinnikov
 */
public class DefaultGroovyCustomizer implements CompilerCustomizer {
    @Override
    public void customize(CompilerConfiguration compilerConfiguration) {
        compilerConfiguration.addCompilationCustomizers(
                new ImportCustomizer()
                        .addImports(
                                Autowired.class.getName(),
                                Resource.class.getName(),
                                Qualifier.class.getName()
                        )
                        .addStarImports(Field.class.getPackage().getName())
        );
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
