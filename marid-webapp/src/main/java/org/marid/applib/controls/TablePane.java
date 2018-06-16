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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.applib.image.ToolIcon;
import org.marid.applib.selection.SelectionManager;
import org.marid.applib.utils.Tables;

import java.util.function.Consumer;

import static org.eclipse.swt.SWT.NONE;
import static org.eclipse.swt.SWT.Selection;
import static org.marid.applib.controls.MaridTable.EventType.ADD;
import static org.marid.applib.controls.MaridTable.EventType.REMOVE;

public class TablePane extends Pane {

  protected final MaridTable table;
  protected final SelectionManager selectionManager;

  public TablePane(Composite parent, int style, int toolbarStyle, int tableStyle) {
    super(parent, style, toolbarStyle);
    table = new MaridTable(this, tableStyle);
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    selectionManager = new SelectionManager(table);

    Tables.autoResizeColumns(table);
  }

  @SafeVarargs
  protected final void addColumn(String text, int width, Consumer<TableColumn>... consumers) {
    final var column = new TableColumn(table, NONE);
    column.setText(text);
    column.setWidth(width);
    for (final var consumer : consumers) {
      consumer.accept(column);
    }
  }

  protected void addSelectAllButton() {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(ToolIcon.SELECT_ALL));
    item.addListener(Selection, e -> selectionManager.selectAll());
    enableOnNonEmpty(item::setEnabled);
  }

  protected void addDeselectAllButton() {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(ToolIcon.DESELECT_ALL));
    item.addListener(Selection, e -> selectionManager.deselectAll());
    enableOnNonEmpty(item::setEnabled);
  }

  protected void enableOnSelection(Consumer<Boolean> enabled) {
    table.addListener(Selection, e -> enabled.accept(selectionManager.isSelected()));
    table.addListener(REMOVE, e -> enabled.accept(selectionManager.isSelected()));
    enabled.accept(selectionManager.isSelected());
  }

  protected void enableOnNonEmpty(Consumer<Boolean> enabled) {
    enabled.accept(table.getItemCount() > 0);
    table.addListener(REMOVE, e -> enabled.accept(table.getItemCount() > 0));
    table.addListener(ADD, e -> enabled.accept(true));
  }
}
