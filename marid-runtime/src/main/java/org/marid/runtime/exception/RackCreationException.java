package org.marid.runtime.exception;

/*-
 * #%L
 * marid-runtime
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

import org.marid.runtime.model.Context;
import org.marid.runtime.model.Rack;

public class RackCreationException extends RuntimeException {

  private final Context context;
  private final Class<? extends Rack> type;

  public RackCreationException(Context context, Class<? extends Rack> type, String message, Throwable cause) {
    super(type.getName() + ": " + message, cause);
    this.context = context;
    this.type = type;
  }

  public RackCreationException(Context context, Class<? extends Rack> type, String message) {
    this(context, type, message, null);
  }

  public Context getContext() {
    return context;
  }

  public Class<? extends Rack> getType() {
    return type;
  }
}
