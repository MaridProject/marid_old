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
package org.marid.applib.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

@FunctionalInterface
public interface SelectListener extends SelectionListener {

  void select(boolean defaultFlag, SelectionEvent event);

  @Override
  default void widgetSelected(SelectionEvent e) {
    select(false, e);
  }

  @Override
  default void widgetDefaultSelected(SelectionEvent e) {
    select(true, e);
  }

  static SelectionListener selectListener(SelectListener listener) {
    return listener;
  }
}
