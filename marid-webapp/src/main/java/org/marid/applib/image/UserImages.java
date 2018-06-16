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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;
import org.marid.app.common.Images;
import org.marid.ui.webide.base.boot.MainEntryPoint;

import java.awt.*;

public class UserImages {

  private static Images images(Widget widget) {
    final var display = widget.getDisplay();
    return (Images) display.getData(MainEntryPoint.USER_IMAGES);
  }

  public static Image image(Widget widget, AppImage image) {
    return images(widget).image(widget.getDisplay(), image);
  }

  public static Image maridIcon(Widget widget, int size, Color color) {
    return images(widget).maridIcon(widget.getDisplay(), size, color);
  }

  public static Image maridIcon(Widget widget, int size) {
    return images(widget).maridIcon(widget.getDisplay(), size);
  }
}
