/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.ui.webide.base.views.projects;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.applib.controls.TablePane;
import org.marid.applib.image.ToolIcon;
import org.marid.misc.StringUtils;
import org.marid.spring.annotation.SpringComponent;
import org.marid.spring.init.Init;
import org.marid.ui.webide.base.common.UserImages;

import static org.eclipse.swt.SWT.*;
import static org.marid.applib.utils.Locales.s;

@SpringComponent
public class ProjectTable extends TablePane {

  private final ProjectManager manager;

  public ProjectTable(ProjectTab tab, ProjectManager manager) {
    super(tab.getParent(), NONE, BORDER | WRAP | SHADOW_OUT, BORDER | V_SCROLL | H_SCROLL | CHECK);
    this.manager = manager;
    tab.setControl(this);
  }

  @Init
  public void nameColumn() {
    final var column = new TableColumn(table, NONE);
    column.setText(s("name"));
    column.setResizable(true);
    column.setMoveable(false);
    column.setWidth(150);
  }

  @Init
  public void sizeColumn() {
    final var column = new TableColumn(table, NONE);
    column.setText(s("size"));
    column.setResizable(true);
    column.setMoveable(false);
    column.setWidth(100);
  }

  @Init
  public void addButton(UserImages images) {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(images.image(ToolIcon.ADD));
  }

  @Init
  public void removeButton(UserImages images) {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(images.image(ToolIcon.REMOVE));
    item.addListener(Selection, e -> manager.remove(table.getSelectionIndices()));
    table.addListener(Selection, e -> item.setEnabled(table.getSelectionCount() > 0));
  }

  @Init
  public void opSeparator() {
    new ToolItem(toolbar, SWT.SEPARATOR);
  }

  @Init
  public void refreshButton(UserImages images) {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(images.image(ToolIcon.REFRESH));
  }

  @Init
  public void updateButton(UserImages images) {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(images.image(ToolIcon.UPDATE));
  }

  @Init
  public void updateSeparator() {
    new ToolItem(toolbar, SWT.SEPARATOR);
  }

  @Init
  public void editButton(UserImages images) {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(images.image(ToolIcon.EDIT));
  }

  @Init
  public void initListeners() {
    manager.addAddListener(e -> e.update.forEach((index, v) -> {
      final TableItem item = new TableItem(table, NONE, index);
      item.setText(new String[] {v.name, StringUtils.sizeBinary(RWT.getLocale(), v.size, 2)});
    }));
    manager.addRemoveListener(e -> e.update.descendingMap().forEach((index, v) -> {
      table.remove(index);
    }));
    manager.addUpdateListener(e -> e.update.forEach((index, v) -> {
      table.getItem(index).setText(new String[] {v.name, StringUtils.sizeBinary(RWT.getLocale(), v.size, 2)});
    }));
  }

  @Init
  public void refresh() {
    manager.refresh();
  }
}
