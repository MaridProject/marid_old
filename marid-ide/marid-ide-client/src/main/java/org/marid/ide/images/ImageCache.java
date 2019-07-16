package org.marid.ide.images;

import org.eclipse.swt.graphics.Image;
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
  private final ConcurrentHashMap<Integer, Image> images = new ConcurrentHashMap<>();

  public ImageCache(Display mainDisplay) {
    this.mainDisplay = mainDisplay;
  }

  public Image image(BufferedImage image) {
    final var bos = new ByteArrayOutputStream();

    try {
      ImageIO.write(image, "PNG", bos);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return new Image(mainDisplay, new ByteArrayInputStream(bos.toByteArray()));
  }

  public Image icon(int size, Color color) {
    return images.computeIfAbsent(size, s -> image(MaridIcon.getImage(s, color)));
  }

  public Image icon(int size) {
    return icon(size, Color.GREEN);
  }

  public Image image(@Language(value = "groovy", prefix = "Thread.currentThread().getContextClassLoader().getResourceAsStream(\"images/", suffix = "\")") String resourcePath) {
    try (final var is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
      return new Image(mainDisplay, is);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
