package org.marid.ide.canvas;

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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.marid.ide.main.MainPane;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("libraryPane")
public class IdeEditor extends Canvas {

  public IdeEditor(MainPane mainPane) {
    super(mainPane, SWT.BORDER);

    setLayout(new RowLayout());

    final var display = getDisplay();
    setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));

    addPaintListener(event -> {
      final var transform = new Transform(display);
      transform.scale(4, 4);

      event.gc.setTransform(transform);
      event.gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
      event.gc.drawLine(0, 0, 50, 50);
    });

    final var button = new Button(this, SWT.PUSH);
    button.setText("ABC");
  }
}
