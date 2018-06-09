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
package org.marid.ui.webide.base.views.artifacts;

import org.eclipse.swt.widgets.TabItem;
import org.marid.app.common.Images;
import org.marid.spring.annotation.SpringComponent;
import org.marid.ui.webide.base.views.repositories.RepositoryTab;

import static org.eclipse.swt.SWT.NONE;
import static org.marid.applib.image.AppIcon.ARTIFACT;
import static org.marid.applib.utils.Locales.s;

@SpringComponent
public class ArtifactTab extends TabItem {

  public ArtifactTab(RepositoryTab tab, Images images) {
    super(tab.getParent(), NONE);
    setText(s("artifacts"));
    setImage(images.icon(this, ARTIFACT));
  }
}
