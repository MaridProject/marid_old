package org.marid.ide.canvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("ideToolbar")
public class IdeCanvas extends Canvas {

  public IdeCanvas(Shell mainShell) {
    super(mainShell, SWT.NONE);

    setLayoutData(new GridData(GridData.FILL_BOTH));
    final var display = mainShell.getDisplay();
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
