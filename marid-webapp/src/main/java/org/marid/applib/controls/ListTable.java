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
package org.marid.applib.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.applib.dao.ListDao;
import org.marid.applib.dao.ListStore;
import org.marid.applib.image.ToolIcon;
import org.marid.applib.model.Id;

import static org.eclipse.swt.SWT.NONE;
import static org.eclipse.swt.SWT.Selection;
import static org.marid.applib.dao.ListStore.EventType.*;
import static org.marid.applib.image.UserImages.image;

public abstract class ListTable<I, T extends Id<I>, M extends ListStore<I, T, ? extends ListDao<I, T>>> extends TablePane {

  protected final M manager;

  public ListTable(M manager, Composite parent, int style, int toolbarStyle, int tableStyle) {
    super(parent, style, toolbarStyle, tableStyle);
    this.manager = manager;

    manager.addListener(ADD, e -> e.update.forEach((index, v) -> {
      final var item = table.new Item(NONE, index);
      item.setText(getRow(v));
      item.setImage(getRowImages(v));
    }));
    manager.addListener(REMOVE, e -> {
      final var map = e.update.descendingMap();
      map.forEach((index, v) -> table.remove(index));
    });
    manager.addListener(UPDATE, e -> e.update.forEach((index, v) -> {
      final var item = table.getItem(index);
      item.setText(getRow(v));
      item.setImage(getRowImages(v));
    }));
  }

  protected abstract String[] getRow(T data);

  protected abstract Image[] getRowImages(T data);

  protected void addStandardButtons() {
    addRemoveButton();
    addSeparator();
    addRefreshButton();
    addSeparator();
    addSelectAllButton();
    addDeselectAllButton();
    addSeparator();
  }

  protected void addRemoveButton() {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(item, ToolIcon.REMOVE));
    item.addListener(Selection, e -> manager.remove(selectionManager.getSelected()));
    enableOnSelection(item::setEnabled);
  }

  protected void addRefreshButton() {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(item, ToolIcon.REFRESH));
    item.addListener(Selection, e -> manager.refresh());
  }
}
