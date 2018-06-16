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
package org.marid.app.common;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.marid.applib.image.AppImage;
import org.marid.image.MaridIcon;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class Images {

  private final Path imageDirectory;
  private final String fallbackImage;
  private final Logger logger;
  private final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Integer, ImageData> maridIcons = new ConcurrentHashMap<>();

  public Images(Directories directories, Logger logger) throws IOException {
    this.imageDirectory = directories.getTempDir().resolve("images");
    this.fallbackImage = directories.getRwtDir()
        .resolve("rwt-resources")
        .resolve("resource")
        .resolve("static")
        .resolve("image")
        .resolve("blank.gif")
        .toString();
    this.logger = logger;

    Files.createDirectory(imageDirectory);
  }

  public String image(AppImage image) {
    return fileMap.computeIfAbsent(image.getImageUrl(), url -> {
      final int lastDotIndex = url.lastIndexOf('.');
      final var suffix = lastDotIndex >= 0 ? url.substring(lastDotIndex) : ".tmp";
      try (final var stream = new URL(url).openStream()) {
        final var file = Files.createTempFile(imageDirectory, "img", suffix);
        Files.copy(stream, file, REPLACE_EXISTING);
        return file.toString();
      } catch (IOException x) {
        logger.warn("Unable to fetch image from {}", url, x);
        return fallbackImage;
      }
    });
  }

  public Image image(Display display, AppImage image) {
    return new Image(display, image(image));
  }

  public Image maridIcon(Display display, int size, int r, int g, int b, int a) {
    final var data = maridIcons.computeIfAbsent(size, s -> {
      final var img = MaridIcon.getImage(size, r, g, b, a);
      final var buf = new ByteArrayOutputStream(size * size * 64);
      try {
        ImageIO.write(img, "gif", buf);
      } catch (IOException x) {
        throw new UncheckedIOException(x);
      }
      return new ImageData(new ByteArrayInputStream(buf.toByteArray()));
    });
    return new Image(display, data);
  }

  public Image maridIcon(Display display, int size, Color color) {
    return maridIcon(display, size, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
  }
}
