package org.marid.ide.canvas;

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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.marid.ide.main.MainPane;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("libraryPane")
public class IdeCanvas extends Canvas {

  public IdeCanvas(MainPane mainPane) {
    super(mainPane, SWT.NONE);
    final var display = mainPane.getDisplay();
    setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));

    addPaintListener(event -> {
      final var transform = new Transform(display);
      transform.scale(4, 4);

      event.gc.setTransform(transform);
      event.gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
      event.gc.drawLine(0, 0, 50, 50);
    });
  }
}
