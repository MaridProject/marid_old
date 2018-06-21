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

  ADD("http://icons.iconarchive.com/icons/awicons/vista-artistic/%d/add-icon.png"),
  REMOVE("http://icons.iconarchive.com/icons/oxygen-icons.org/oxygen/%d/Actions-list-remove-icon.png"),
  REFRESH("http://icons.iconarchive.com/icons/oxygen-icons.org/oxygen/%d/Actions-view-refresh-icon.png"),
  UPDATE("http://icons.iconarchive.com/icons/visualpharm/must-have/%d/Refresh-icon.png"),
  EDIT("http://icons.iconarchive.com/icons/turbomilk/livejournal-10/%d/pencil-icon.png"),
  PROJECT("http://icons.iconarchive.com/icons/oxygen-icons.org/oxygen/%d/Actions-project-development-new-template-icon.png"),
  SELECT_ALL("http://icons.iconarchive.com/icons/fatcow/farm-fresh/%d/layer-select-icon.png"),
  DESELECT_ALL("http://icons.iconarchive.com/icons/fatcow/farm-fresh/%d/select-invert-icon.png"),
  PREFERENCES("http://icons.iconarchive.com/icons/bokehlicia/captiva/%d/preferences-icon.png"),
  FIND("http://icons.iconarchive.com/icons/graphicloads/100-flat/%d/zoom-seach-icon.png"),
  CANCEL("http://icons.iconarchive.com/icons/rafiqul-hassan/blogger/%d/Close-icon.png"),
  ARTIFACT("http://icons.iconarchive.com/icons/artdesigner/my-secret/%d/diamond-icon.png"),
  REPOSITORY("http://icons.iconarchive.com/icons/oxygen-icons.org/oxygen/%d/Places-repository-icon.png"),
  SELECTOR("http://icons.iconarchive.com/icons/papirus-team/papirus-apps/%d/chromium-app-list-icon.png");

  private final String url;

  ToolIcon(String url) {
    this.url = url;
  }

  @Override
  public @NotNull String getImageUrl(int size) {
    return String.format(url, size);
  }
}
