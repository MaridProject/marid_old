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
package org.marid.ui.webide.base.common;

import org.eclipse.swt.graphics.Image;
import org.marid.app.common.Images;
import org.marid.applib.image.AppImage;
import org.marid.ui.webide.base.UI;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class UserImages {

  private final Images images;
  private final UI ui;

  public UserImages(Images images, UI ui) {
    this.images = images;
    this.ui = ui;
  }

  public Image image(AppImage image) {
    return images.image(ui.getShell(), image);
  }

  public Image maridIcon(int size, Color color) {
    return images.maridIcon(ui.getShell(), size, color);
  }

  public Image maridIcon(int size) {
    return images.maridIcon(ui.getShell(), size);
  }
}
