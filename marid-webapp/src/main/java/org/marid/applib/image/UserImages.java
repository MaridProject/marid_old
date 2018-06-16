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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.marid.app.common.Images;

import java.awt.*;

import static org.marid.ui.webide.base.boot.MainEntryPoint.USER_IMAGES;

public class UserImages {

  private final Images images;
  private final Display display;

  public UserImages(Images images, Display display) {
    this.images = images;
    this.display = display;
  }

  public Image image(AppImage image) {
    return images.image(display, image);
  }

  public Image maridIcon(int size, Color color) {
    return images.maridIcon(display, size, color);
  }

  public Image maridIcon(int size) {
    return images.maridIcon(display, size);
  }

  public static UserImages images(Widget widget) {
    final var display = widget.getDisplay();
    return (UserImages) display.getData(USER_IMAGES);
  }

  public static Image image(Widget widget, AppImage image) {
    return images(widget).image(image);
  }

  public static Image maridIcon(Widget widget, int size, Color color) {
    return images(widget).maridIcon(size, color);
  }

  public static Image maridIcon(Widget widget, int size) {
    return images(widget).maridIcon(size);
  }
}
