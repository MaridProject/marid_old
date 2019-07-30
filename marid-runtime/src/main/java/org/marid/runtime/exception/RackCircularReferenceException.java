package org.marid.runtime.exception;

import org.marid.runtime.model.Context;
import org.marid.runtime.model.Rack;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RackCircularReferenceException extends RackCreationException {

  public RackCircularReferenceException(Context context, Class<? extends Rack> type, Class<?>... passed) {
    super(context, type, Arrays.stream(passed).map(Class::getName).collect(Collectors.joining(",", "Circular reference: [", "]")));
  }
}
