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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.applib.controls.ListTable;
import org.marid.applib.dialogs.Dialogs;
import org.marid.applib.image.AppIcon;
import org.marid.applib.image.ToolIcon;
import org.marid.applib.model.ProjectItem;
import org.marid.spring.annotation.SpringComponent;
import org.marid.spring.init.Init;

import java.util.List;

import static org.eclipse.swt.SWT.*;
import static org.marid.applib.image.UserImages.image;
import static org.marid.applib.utils.Locales.m;
import static org.marid.applib.utils.Locales.s;
import static org.marid.applib.validators.InputValidators.*;
import static org.marid.misc.StringUtils.sizeBinary;

@SpringComponent
public class ProjectTable extends ListTable<String, ProjectItem, ProjectManager> {

  public ProjectTable(ProjectTab tab, ProjectManager manager) {
    super(manager, tab.getParent(), NONE, BORDER | WRAP | FLAT, BORDER | V_SCROLL | H_SCROLL | CHECK);
    tab.setControl(this);
    table.setLinesVisible(true);
    addColumn(s("name"), 150);
    addColumn(s("size"), 100);
    manager.refresh();
  }

  @Init
  public void addButton() {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(item, ToolIcon.ADD));
    item.addListener(Selection, e -> Dialogs.input()
        .setIcon(AppIcon.PROJECT)
        .setShell(getShell())
        .setMessage(m("newProjectName") + ":")
        .setTitle(s("addProject"))
        .setValue("project")
        .setValidator(inputs(fileName(), input(o -> o.filter(manager::contains).map(id -> m("duplicateItem", id)))))
        .setCallback(v -> v.ifPresent(txt -> manager.add(List.of(new ProjectItem(txt)))))
        .open()
    );
  }

  @Init
  @Override
  public void addStandardButtons() {
    super.addStandardButtons();
  }

  @Init
  public void editButton() {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(item, ToolIcon.EDIT));
    enableOnSelection(item::setEnabled);
  }

  @Override
  protected String[] getRow(ProjectItem item) {
    return new String[]{item.getId(), sizeBinary(RWT.getLocale(), manager.getSize(item.getId()), 2)};
  }

  @Override
  protected Image[] getRowImages(ProjectItem data) {
    return new Image[]{image(this, AppIcon.PROJECT), null};
  }
}
