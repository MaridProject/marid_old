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

public enum ToolIcon implements AppImage {

  ADD("http://icons.iconarchive.com/icons/awicons/vista-artistic/24/add-icon.png"),
  REMOVE("http://icons.iconarchive.com/icons/oxygen-icons.org/oxygen/24/Actions-list-remove-icon.png"),
  REFRESH("http://icons.iconarchive.com/icons/oxygen-icons.org/oxygen/24/Actions-view-refresh-icon.png"),
  UPDATE("http://icons.iconarchive.com/icons/visualpharm/must-have/24/Refresh-icon.png"),
  EDIT("http://icons.iconarchive.com/icons/turbomilk/livejournal-10/24/pencil-icon.png"),
  PROJECT("http://icons.iconarchive.com/icons/oxygen-icons.org/oxygen/24/Actions-project-development-new-template-icon.png"),
  SELECT_ALL("http://icons.iconarchive.com/icons/fatcow/farm-fresh/24/layer-select-icon.png"),
  DESELECT_ALL("http://icons.iconarchive.com/icons/fatcow/farm-fresh/24/select-invert-icon.png");

  private final String url;

  ToolIcon(String url) {
    this.url = url;
  }

  @NotNull
  @Override
  public String getImageUrl() {
    return url;
  }
}
