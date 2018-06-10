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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.applib.image.ToolIcon;
import org.marid.spring.annotation.SpringComponent;
import org.marid.spring.init.Init;
import org.marid.ui.webide.base.common.UserImages;

import static org.eclipse.swt.SWT.*;

@SpringComponent
public class ProjectToolbar extends ToolBar {

  private final ProjectManager manager;

  public ProjectToolbar(ProjectTab tab, ProjectManager manager) {
    super(tab.panel, BORDER | WRAP | SHADOW_OUT);
    this.manager = manager;
    setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
  }

  @Init
  public void addButton(UserImages images) {
    final var item = new ToolItem(this, SWT.PUSH);
    item.setImage(images.image(ToolIcon.ADD));
  }

  @Init
  public void removeButton(UserImages images, ProjectTable table) {
    final var item = new ToolItem(this, SWT.PUSH);
    item.setImage(images.image(ToolIcon.REMOVE));
    item.addListener(Selection, e -> manager.remove(table.getSelectionIndices()));
    table.addListener(Selection, e -> item.setEnabled(table.getSelectionCount() > 0));
  }

  @Init
  public void opSeparator() {
    new ToolItem(this, SWT.SEPARATOR);
  }

  @Init
  public void refreshButton(UserImages images) {
    final var item = new ToolItem(this, SWT.PUSH);
    item.setImage(images.image(ToolIcon.REFRESH));
  }

  @Init
  public void updateButton(UserImages images) {
    final var item = new ToolItem(this, SWT.PUSH);
    item.setImage(images.image(ToolIcon.UPDATE));
  }

  @Init
  public void updateSeparator() {
    new ToolItem(this, SWT.SEPARATOR);
  }

  @Init
  public void editButton(UserImages images) {
    final var item = new ToolItem(this, SWT.PUSH);
    item.setImage(images.image(ToolIcon.EDIT));
  }
}
