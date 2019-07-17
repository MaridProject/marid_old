package org.marid.ide.canvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;
import org.marid.ide.spring.SpringWidget;
import org.springframework.stereotype.Component;

@Component
public class IdeCanvas extends SpringWidget<Canvas> {

  public IdeCanvas(Shell mainShell) {
    super(new Canvas(mainShell, SWT.NONE));

    widget.setLayoutData(new GridData(GridData.FILL_BOTH));
    final var display = mainShell.getDisplay();
    widget.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
  }
}
