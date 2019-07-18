package org.marid.ide.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("projectMenu")
public class LibraryMenu extends Menu {

  public LibraryMenu(Shell mainShell, MenuBar menuBar) {
    super(mainShell, SWT.DROP_DOWN);
    final var menuItem = new MenuItem(menuBar, SWT.CASCADE);
    menuItem.setText("Library");
    menuItem.setMenu(this);
  }

  @Override
  protected void checkSubclass() {
  }
}
