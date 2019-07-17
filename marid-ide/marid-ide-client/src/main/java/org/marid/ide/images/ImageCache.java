package org.marid.ide.images;

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
    final var bos = new ByteArrayOutputStream(8192);
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

  public Image image(@Language(value = "groovy", prefix = "Thread.currentThread().getContextClassLoader().getResourceAsStream(\"images/", suffix = "\")") String resourcePath) {
    return new Image(mainDisplay, images.computeIfAbsent(resourcePath, p -> {
      try (final var is = Thread.currentThread().getContextClassLoader().getResourceAsStream(p)) {
        return new ImageData(is);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }));
  }
}
