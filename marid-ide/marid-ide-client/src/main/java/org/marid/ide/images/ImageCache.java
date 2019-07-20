package org.marid.ide.images;

/*-
 * #%L
 * marid-ide-client
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
