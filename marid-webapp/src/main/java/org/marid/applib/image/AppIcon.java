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
package org.marid.applib.image;

import org.jetbrains.annotations.NotNull;

public enum AppIcon implements AppImage {

  PROJECT("http://icons.iconarchive.com/icons/oxygen-icons.org/oxygen/16/Actions-project-development-new-template-icon.png"),
  ARTIFACT("http://icons.iconarchive.com/icons/artdesigner/my-secret/16/diamond-icon.png"),
  REPOSITORY("http://icons.iconarchive.com/icons/oxygen-icons.org/oxygen/16/Places-repository-icon.png");

  private final String url;

  AppIcon(@NotNull String url) {
    this.url = url;
  }

  @NotNull
  @Override
  public String getImageUrl() {
    return url;
  }
}
