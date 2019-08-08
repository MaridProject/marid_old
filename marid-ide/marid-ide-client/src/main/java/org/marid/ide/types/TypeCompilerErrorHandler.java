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

import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;

public final class TypeCompilerErrorHandler implements IErrorHandlingPolicy {

  public static final TypeCompilerErrorHandler DEFAULT_ERROR_HANDLER = new TypeCompilerErrorHandler(false, true, false);

  private final boolean proceedOnErrors;
  private final boolean stopOnFirstError;
  private final boolean ignoreAllErrors;

  public TypeCompilerErrorHandler(boolean proceedOnErrors, boolean stopOnFirstError, boolean ignoreAllErrors) {
    this.proceedOnErrors = proceedOnErrors;
    this.stopOnFirstError = stopOnFirstError;
    this.ignoreAllErrors = ignoreAllErrors;
  }

  @Override
  public boolean proceedOnErrors() {
    return proceedOnErrors;
  }

  @Override
  public boolean stopOnFirstError() {
    return stopOnFirstError;
  }

  @Override
  public boolean ignoreAllErrors() {
    return ignoreAllErrors;
  }
}
