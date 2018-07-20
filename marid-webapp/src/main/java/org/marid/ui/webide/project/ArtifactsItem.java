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
package org.marid.ui.webide.project;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeItem;
import org.marid.applib.image.IaIcon;
import org.marid.applib.image.WithImages;
import org.marid.spring.orders.Orders;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static org.marid.applib.utils.Locales.s;

@Component
@Order(1)
public class ArtifactsItem extends TreeItem implements WithImages {

  public ArtifactsItem(ProjectTree tree) {
    super(tree, SWT.NONE, Orders.index(tree::getItem, tree.getItemCount()));
    setText(s("artifacts"));
    setImage(image(IaIcon.ARTIFACT, 16));
  }
}
