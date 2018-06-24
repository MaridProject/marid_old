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
package org.marid.ui.webide.base.projects;

import org.eclipse.rap.rwt.RWT;
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
import static org.marid.misc.StringUtils.sizeBinary;

@Component
public class ProjectTable extends TablePane {

  public ProjectTable(ProjectTab tab) {
    super(tab.getParent());
    tab.setControl(this);
    addColumn(s("name"), 200);
    addColumn(s("size"), 100);
  }

  @Init
  public void initStore(ProjectStore store) {
    final Consumer<Item> itemSetup = item -> {
      final var e = store.get(table.indexOf(item));
      item.setTexts(e.getId(), sizeBinary(RWT.getLocale(), store.getSize(e.getId()), 2));
      item.setImages(image(IaIcon.PROJECT, 16), null);
    };
    store.addListener(ADD, e -> e.update.forEach((index, v) -> itemSetup.accept(table.new Item(NONE, index))));
    store.addListener(REMOVE, e -> e.update.descendingMap().forEach((index, v) -> table.remove(index)));
    store.addListener(UPDATE, e -> e.update.forEach((index, v) -> itemSetup.accept((Item) table.getItem(index))));
    store.refresh();
  }

  @Init
  public void addButton(ObjectFactory<ProjectAddDialog> dialogFactory) {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(IaIcon.ADD));
    item.addListener(Selection, e -> dialogFactory.getObject().open());
  }

  @Init
  public void removeButton(ProjectStore store) {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(IaIcon.REMOVE));
    item.addListener(Selection, e -> store.remove(selectionManager.getSelected()));
    enableOnSelection(item::setEnabled);
  }

  @Init
  public void refreshButton(ProjectStore store) {
    addSeparator();
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(IaIcon.REFRESH));
    item.addListener(Selection, e -> store.refresh());
  }

  @Init
  public void saveButton(ProjectStore store) {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(IaIcon.SAVE));
    item.addListener(Selection, e -> store.save());
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
