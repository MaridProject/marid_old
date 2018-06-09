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

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public interface Listeners {

  static SelectListener select(SelectListener listener) {
    return listener;
  }

  static DefaultSelectListener defaultSelect(DefaultSelectListener listener) {
    return listener;
  }

  static ResizeListener resize(ResizeListener listener) {
    return listener;
  }

  static MoveListener move(MoveListener listener) {
    return listener;
  }

  @FunctionalInterface
  interface SelectListener extends SelectionListener {

    @Override
    default void widgetDefaultSelected(SelectionEvent e) {
    }
  }

  @FunctionalInterface
  interface DefaultSelectListener extends SelectionListener {

    @Override
    default void widgetSelected(SelectionEvent e) {
    }
  }

  @FunctionalInterface
  interface ResizeListener extends ControlListener {

    @Override
    default void controlMoved(ControlEvent e) {
    }
  }

  @FunctionalInterface
  interface MoveListener extends ControlListener {

    @Override
    default void controlResized(ControlEvent e) {
    }
  }
}
