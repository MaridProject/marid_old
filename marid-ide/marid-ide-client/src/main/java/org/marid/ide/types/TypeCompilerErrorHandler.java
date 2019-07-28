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
