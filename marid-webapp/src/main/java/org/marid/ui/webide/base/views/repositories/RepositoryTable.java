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
package org.marid.ui.webide.base.views.repositories;

import org.eclipse.swt.graphics.Image;
import org.marid.applib.controls.ListTable;
import org.marid.applib.image.AppIcon;
import org.marid.applib.model.RepositoryItem;
import org.springframework.stereotype.Component;

import static org.marid.applib.utils.Locales.s;

@Component
public class RepositoryTable extends ListTable<String, RepositoryItem, RepositoryManager> {

  public RepositoryTable(RepositoryManager manager, RepositoryTab tab) {
    super(manager, tab.getParent());
    tab.setControl(this);
    addColumn(s("name"), 100);
    addColumn(s("selector"), 100);
    manager.refresh();
  }

  @Override
  protected String[] getRow(RepositoryItem data) {
    return new String[] {data.getId(), data.getSelector()};
  }

  @Override
  protected Image[] getRowImages(RepositoryItem data) {
    return new Image[] {image(AppIcon.REPOSITORY), null};
  }
}
