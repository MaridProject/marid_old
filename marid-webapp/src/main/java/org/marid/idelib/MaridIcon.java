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
package org.marid.idelib;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;

public enum MaridIcon {

  CLOSE("/svg/cross.svg"),
  SESSION_CLOSE("/svg/sessionClose.svg")
  ;

  private final String url;

  MaridIcon(String url) {
    this.url = url;
  }

  public IconImage newIcon() {
    return new IconImage(url);
  }

  @Tag("iron-icon")
  @HtmlImport("frontend://bower_components/vaadin-icons/vaadin-icons.html")
  public static class IconImage extends Component implements HasStyle {

    private IconImage(String src) {
      getElement().setAttribute("src", src);
      getElement().setAttribute("style", "margin-right: 6px");
    }
  }
}
