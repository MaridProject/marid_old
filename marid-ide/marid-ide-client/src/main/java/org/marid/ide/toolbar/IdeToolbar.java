package org.marid.ide.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.springframework.stereotype.Component;

@Component
public class IdeToolbar extends ToolBar {

  public IdeToolbar(Shell mainShell) {
    super(mainShell, SWT.HORIZONTAL);

    setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
  }


  @Override
  protected void checkSubclass() {
  }
}
