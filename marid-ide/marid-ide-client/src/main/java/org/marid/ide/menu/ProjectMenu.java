package org.marid.ide.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.marid.spring.init.Init;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@Order(1)
public class ProjectMenu implements Supplier<Menu> {

  private final Menu menu;

  public ProjectMenu(Shell mainShell, MenuBar menuBar) {
    menu = new Menu(mainShell, SWT.DROP_DOWN);
    final var menuItem = new MenuItem(menuBar.get(), SWT.CASCADE);
    menuItem.setText("&Project");
    menuItem.setMenu(menu);
  }

  @Init
  public void initNewProjectItem() {
    final var item = new MenuItem(menu, SWT.PUSH);
    item.setText("New project");
    item.addListener(SWT.Selection, e -> {});
    new MenuItem(menu, SWT.SEPARATOR);
  }

  @Init
  public void initExitItem(ObjectFactory<Runnable> exitCommand) {
    final var item = new MenuItem(menu, SWT.PUSH);
    item.setText("E&xit");
    item.addListener(SWT.Selection, e -> exitCommand.getObject().run());
  }

  @Override
  public Menu get() {
    return menu;
  }
}
