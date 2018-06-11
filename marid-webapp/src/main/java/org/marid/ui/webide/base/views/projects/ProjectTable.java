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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.marid.applib.dao.ListManager;
import org.marid.applib.utils.Tables;
import org.marid.misc.StringUtils;
import org.marid.spring.annotation.SpringComponent;
import org.marid.spring.init.Init;
import org.marid.ui.webide.base.dao.ProjectDao;
import org.marid.ui.webide.base.model.ProjectItem;

import java.util.function.Consumer;

import static org.eclipse.swt.SWT.*;
import static org.marid.applib.utils.Locales.s;

@SpringComponent
public class ProjectTable extends Table implements AutoCloseable {

  private final ProjectManager manager;
  private final Consumer<ListManager<ProjectDao, ProjectItem>.Event> addListener;
  private final Consumer<ListManager<ProjectDao, ProjectItem>.Event> removeListener;
  private final Consumer<ListManager<ProjectDao, ProjectItem>.Event> updateListener;

  public ProjectTable(ProjectTab tab, ProjectManager manager) {
    super(tab.panel, BORDER | V_SCROLL | H_SCROLL);
    this.manager = manager;
    setHeaderVisible(true);
    setLayoutData(new GridData(GridData.FILL_BOTH));

    Tables.autoResizeColumns(this);

    addListener = manager.addAddListener(e -> e.update.forEach((index, v) -> {
      final TableItem item = new TableItem(this, NONE, index);
      item.setText(new String[] {v.name, StringUtils.sizeBinary(RWT.getLocale(), v.size, 2)});
    }));
    removeListener = manager.addRemoveListener(e -> e.update.descendingMap().forEach((index, v) -> {
      remove(index);
    }));
    updateListener = manager.addUpdateListener(e -> e.update.forEach((index, v) -> {
      getItem(index).setText(new String[] {v.name, StringUtils.sizeBinary(RWT.getLocale(), v.size, 2)});
    }));
  }

  @Init
  public void nameColumn() {
    final var column = new TableColumn(this, NONE);
    column.setText(s("name"));
    column.setResizable(true);
    column.setMoveable(false);
    column.setWidth(150);
  }

  @Init
  public void sizeColumn() {
    final var column = new TableColumn(this, NONE);
    column.setText(s("size"));
    column.setResizable(true);
    column.setMoveable(false);
    column.setWidth(100);
  }

  @Init
  public void items() {
    manager.refresh();
  }

  @Override
  public void close() {
    manager.removeAddListener(addListener);
    manager.removeRemoveListener(removeListener);
    manager.removeUpdateListener(updateListener);
  }
}
