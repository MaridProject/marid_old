/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.applib.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;

import java.util.stream.IntStream;

public interface Tables {

  static void autoResizeColumns(Table table) {
    table.addListener(SWT.Resize, e -> {
      final int sum = IntStream.range(0, table.getColumnCount()).map(i -> table.getColumn(i).getWidth()).sum();
      final int width = Math.max(0, table.getBounds().width - 16);
      for (int i = 0; i < table.getColumnCount(); i++) {
        final var column = table.getColumn(i);
        column.setWidth((width * column.getWidth()) / sum);
      }
    });
  }
}
