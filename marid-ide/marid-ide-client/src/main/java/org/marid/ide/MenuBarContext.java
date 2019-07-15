package org.marid.ide;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MenuBarContext {

  @Bean
  public Menu menuBar(Shell mainShell) {
    final var menu = new Menu(mainShell, SWT.BAR | SWT.BORDER);
    mainShell.setMenuBar(menu);
    return menu;
  }

  @Bean
  public Menu projectMenu(Shell mainShell, Menu menuBar) {
    final var menu = new Menu(mainShell, SWT.DROP_DOWN);
    final var menuItem = new MenuItem(menuBar, SWT.CASCADE);
    menuItem.setText("&Project");
    menuItem.setMenu(menu);
    return menu;
  }

  @Bean
  public MenuItem newProjectItem(Menu projectMenu) {
    final var item = new MenuItem(projectMenu, SWT.PUSH);
    item.setText("New project");
    item.addListener(SWT.Selection, e -> {});
    return item;
  }

  @Bean
  public MenuItem newProjectItemSeparator(Menu projectMenu) {
    return new MenuItem(projectMenu, SWT.SEPARATOR);
  }

  @Bean
  public MenuItem exitItem(Menu projectMenu, Shell mainShell) {
    final var item = new MenuItem(projectMenu, SWT.PUSH);
    item.setText("E&xit");
    item.addListener(SWT.Selection, e -> mainShell.close());
    return item;
  }
}
