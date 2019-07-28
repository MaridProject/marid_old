package org.marid.ide.types;

/*-
 * #%L
 * marid-ide-client
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
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

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.springframework.stereotype.Component;

@Component
public class TypeCompilerOptions extends CompilerOptions {

  public TypeCompilerOptions() {
    processAnnotations = false;
    produceMethodParameters = true;
    produceReferenceInfo = true;
    complianceLevel = originalComplianceLevel = sourceLevel = originalSourceLevel = targetJDK = ClassFileConstants.JDK12;
    defaultEncoding = "UTF-8";
    preserveAllLocalVariables = true;
    generateClassFiles = false;
  }
}
