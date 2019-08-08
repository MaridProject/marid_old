package org.marid.ide.images;

/*-
 * #%L
 * marid-ide-client
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.intellij.lang.annotations.Language;
import org.marid.image.MaridIcon;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ImageCache {

  private final Display mainDisplay;
  private final ConcurrentHashMap<String, ImageData> images = new ConcurrentHashMap<>();

  public ImageCache(Display mainDisplay) {
    this.mainDisplay = mainDisplay;
  }

  private ImageData imageData(BufferedImage image) {
    final var bos = new ByteArrayOutputStream(image.getWidth() * image.getHeight() * 32 + 512);
    try {
      ImageIO.write(image, "PNG", bos);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return new ImageData(new ByteArrayInputStream(bos.toByteArray()));
  }

  public Image icon(int size, Color color) {
    return new Image(mainDisplay, images.computeIfAbsent("icon" + size, s -> imageData(MaridIcon.getImage(size, color))));
  }

  public Image icon(int size) {
    return icon(size, Color.GREEN);
  }

  public Image image(@Language(value = "java", prefix = "class X {void x() {getClass().getResource(\"/images/", suffix = "\");}}") String resourcePath) {
    return new Image(mainDisplay, images.computeIfAbsent(resourcePath, p -> {
      try (final var is = Thread.currentThread().getContextClassLoader().getResourceAsStream("images/" + p)) {
        return new ImageData(is);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }));
  }
}
