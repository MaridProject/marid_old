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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.applib.controls.pane.TablePane;
import org.marid.applib.image.IaIcon;
import org.marid.applib.utils.Tables;
import org.marid.spring.init.Init;
import org.marid.ui.webide.base.dao.ProjectStore;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import static org.eclipse.swt.SWT.Selection;
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
    Tables.init(this, store, (item, e) -> {
      item.setText(new String[] {e.getId(), sizeBinary(RWT.getLocale(), store.getSize(e.getId()), 2)});
      item.setImage(new Image[] {image(IaIcon.PROJECT, 16), null});
    });
  }

  @Init
  public void addButton(ObjectFactory<ProjectAddDialog> dialogFactory) {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(IaIcon.ADD));
    item.addListener(Selection, e -> dialogFactory.getObject().open());
  }

  @Init
  public void stdButtons(ProjectStore store) {
    Tables.addStdButtons(this, store);
  }

  @Init
  public void editButton() {
    final var item = new ToolItem(toolbar, SWT.PUSH);
    item.setImage(image(IaIcon.EDIT));
    enableOnSelection(item::setEnabled);
  }
}
