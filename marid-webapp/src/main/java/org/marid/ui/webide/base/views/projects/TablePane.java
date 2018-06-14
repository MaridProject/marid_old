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
import org.marid.misc.StringUtils;
import org.marid.spring.annotation.SpringComponent;
import org.marid.spring.init.Init;
import org.marid.ui.webide.base.common.UserImages;
import org.marid.applib.model.ProjectItem;

import java.util.List;

import static org.eclipse.swt.SWT.*;
import static org.marid.applib.utils.Locales.m;
import static org.marid.applib.utils.Locales.s;
import static org.marid.applib.validators.InputValidators.*;

@SpringComponent
public class TablePane extends ListTable<String, ProjectItem, ProjectManager> {

  public TablePane(ProjectTab tab, UserImages images, ProjectManager manager) {
    super(manager, images, tab.getParent(), NONE, BORDER | WRAP | FLAT, BORDER | V_SCROLL | H_SCROLL | CHECK);
    tab.setControl(this);
    table.setLinesVisible(true);
    addColumn(s("name"), 150);
    addColumn(s("size"), 100);
    manager.refresh();
  }

  @Init
  public void addButton(UserImages images) {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(images.image(ToolIcon.ADD));
    item.addListener(Selection, e -> Dialogs.input(images)
        .setIcon(AppIcon.PROJECT)
        .setShell(getShell())
        .setMessage(m("newProjectName") + ":")
        .setTitle(s("addProject"))
        .setValue("project")
        .setValidator(inputs(fileName(), input(o -> o.filter(manager::contains).map(id -> m("duplicateItem", id)))))
        .setCallback(v -> v.ifPresent(txt -> manager.add(List.of(new ProjectItem(txt, 0L)))))
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
    item.setImage(images.image(ToolIcon.EDIT));
    setupSelectionEnabled(item);
  }

  @Override
  protected String[] getRow(ProjectItem item) {
    return new String[]{item.name, StringUtils.sizeBinary(RWT.getLocale(), item.size, 2)};
  }

  @Override
  protected Image[] getRowImages(ProjectItem data) {
    return new Image[]{null, null};
  }
}
