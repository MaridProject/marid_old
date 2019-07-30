package org.marid.runtime.exception;

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
