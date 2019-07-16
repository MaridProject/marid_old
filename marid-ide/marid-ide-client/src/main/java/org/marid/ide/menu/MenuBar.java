package org.marid.ide.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class MenuBar implements Supplier<Menu> {

  private final Menu menu;

  public MenuBar(Shell mainShell) {
    this.menu = new Menu(mainShell, SWT.BAR);
    mainShell.setMenuBar(menu);
  }

  @Override
  public Menu get() {
    return menu;
  }
}
