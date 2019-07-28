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

import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

import java.io.PrintWriter;
import java.io.Writer;

import static org.marid.ide.types.TypeCompilerErrorHandler.DEFAULT_ERROR_HANDLER;

public class TypeCompiler extends Compiler {

  public TypeCompiler(FileSystem fileSystem, CompilerOptions options, ICompilerRequestor requestor, IProblemFactory problemFactory, PrintWriter writer) {
    super(fileSystem, DEFAULT_ERROR_HANDLER, options, requestor, problemFactory, writer, null);
  }

  public TypeCompiler(FileSystem fileSystem, CompilerOptions options, ICompilerRequestor requestor, IProblemFactory problemFactory, Writer writer) {
    this(fileSystem, options, requestor, problemFactory, new PrintWriter(writer, true));
  }
}
