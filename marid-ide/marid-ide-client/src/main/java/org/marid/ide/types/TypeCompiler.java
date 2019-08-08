package org.marid.ide.types;

/*-
 * #%L
 * marid-ide-client
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
