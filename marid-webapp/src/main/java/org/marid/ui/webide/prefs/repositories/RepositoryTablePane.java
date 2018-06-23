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
package org.marid.ui.webide.prefs.repositories;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.applib.controls.pane.TablePane;
import org.marid.applib.controls.table.MaridTable.Item;
import org.marid.applib.image.IaIcon;
import org.marid.spring.init.Init;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static org.eclipse.swt.SWT.NONE;
import static org.eclipse.swt.SWT.Selection;
import static org.marid.applib.dao.ListStore.EventType.*;
import static org.marid.applib.utils.Locales.s;

@Component
public class RepositoryTablePane extends TablePane {

  public RepositoryTablePane(RepositoryTab tab) {
    super(tab.getParent());
    tab.setControl(this);
    addColumn(s("name"), 100);
    addColumn(s("selector"), 100);
    addColumn(s("parameters"), 100);
  }

  @Init
  public void initStore(RepositoryStore store) {
    final Consumer<Item> itemSetup = item -> {
      final var e = store.get(table.indexOf(item));
      item.setTexts(e.getId(), e.getSelector());
      item.setImages(image(IaIcon.REPOSITORY, 16), null);
    };
    store.addListener(ADD, e -> e.update.forEach((index, v) -> itemSetup.accept(table.new Item(NONE, index))));
    store.addListener(REMOVE, e -> e.update.descendingMap().forEach((index, v) -> table.remove(index)));
    store.addListener(UPDATE, e -> e.update.forEach((index, v) -> itemSetup.accept((Item) table.getItem(index))));
    store.refresh();
  }

  @Init
  public void addButton(ObjectFactory<RepositoryAddDialog> dialog) {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(IaIcon.ADD));
    item.addListener(Selection, e -> dialog.getObject().open());
  }

  @Init
  public void removeButton(RepositoryStore store) {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(IaIcon.REMOVE));
    item.addListener(Selection, e -> store.remove(selectionManager.getSelected()));
    enableOnSelection(item::setEnabled);
  }

  @Init
  public void refreshButtons(RepositoryStore store) {
    addSeparator();
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(IaIcon.REFRESH));
    item.addListener(Selection, e -> store.refresh());
  }

  @Init
  public void stdButtons() {
    addSeparator();
    addSelectAllButton();
    addDeselectAllButton();
    addSeparator();
  }

  @Init
  public void editButton() {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(IaIcon.EDIT));
    enableOnSelection(item::setEnabled);
  }
}
