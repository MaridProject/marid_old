package org.marid.ide.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.springframework.stereotype.Component;

@Component
public class MenuBar extends Menu {

  public MenuBar(Shell mainShell) {
    super(mainShell, SWT.BAR);

    mainShell.setMenuBar(this);
  }

  @Override
  protected void checkSubclass() {
  }
}
