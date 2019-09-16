package org.marid.project.typeresolver;

/*-
 * #%L
 * marid-ide-model
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

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJrt;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicReference;

class TypeResolverTest {

  private static FileSystem.Classpath[] classpaths() {
    return new FileSystem.Classpath[] {
        new ClasspathJrt(new File(System.getProperty("java.home")), true, null, null)
    };
  }

  private static CompilerOptions compilerOptions() {
    final var options = new CompilerOptions();
    options.complianceLevel
        = options.originalComplianceLevel
        = options.sourceLevel
        = options.originalSourceLevel
        = options.targetJDK
        = ClassFileConstants.JDK12;
    options.generateClassFiles = false;
    options.storeAnnotations = true;
    options.defaultEncoding = "UTF-8";
    options.performMethodsFullRecovery = true;
    options.performStatementsRecovery = true;
    options.enablePreviewFeatures = true;
    options.preserveAllLocalVariables = true;
    return options;
  }

  @Test
  void resolve() {
    final var fs = new FileSystem(classpaths(), new String[] {}, true) {
    };
    final var errorHandling = Mockito.mock(IErrorHandlingPolicy.class);
    final var result = new AtomicReference<CompilationResult>();
    final var writer = new StringWriter();
    final var printer = new PrintWriter(writer);
    final var progress = Mockito.mock(CompilationProgress.class);
    final var problems = new DefaultProblemFactory();
    final var compiler = new Compiler(fs, errorHandling, compilerOptions(), result::set, problems, printer, progress);

    final var compilationResult = new CompilationResult(new char[0], 0, 1, 1);
    final var compilationUnionDeclaration = new CompilationUnitDeclaration(compiler.problemReporter, compilationResult, 0);

  }
}
