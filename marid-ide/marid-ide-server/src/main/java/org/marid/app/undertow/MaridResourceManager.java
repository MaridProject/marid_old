package org.marid.app.undertow;

import io.undertow.server.handlers.resource.*;
import org.marid.app.common.Directories;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MaridResourceManager implements ResourceManager {

  private final ResourceManager metaInf;
  private final ResourceManager rwt;

  public MaridResourceManager(Directories directories) {
    metaInf = new ClassPathResourceManager(Thread.currentThread().getContextClassLoader(), "META-INF/resources");
    rwt = new PathResourceManager(directories.getRwtDir());
  }

  @Override
  public Resource getResource(String path) throws IOException {
    if (path.startsWith("/rwt-resources/")) {
      return rwt.getResource(path);
    } else {
      return metaInf.getResource(path);
    }
  }

  @Override
  public boolean isResourceChangeListenerSupported() {
    return false;
  }

  @Override
  public void registerResourceChangeListener(ResourceChangeListener listener) {
    if (metaInf.isResourceChangeListenerSupported()) {
      metaInf.registerResourceChangeListener(listener);
    }

    if (rwt.isResourceChangeListenerSupported()) {
      rwt.registerResourceChangeListener(listener);
    }
  }

  @Override
  public void removeResourceChangeListener(ResourceChangeListener listener) {
    if (metaInf.isResourceChangeListenerSupported()) {
      metaInf.removeResourceChangeListener(listener);
    }

    if (rwt.isResourceChangeListenerSupported()) {
      rwt.removeResourceChangeListener(listener);
    }
  }

  @Override
  public void close() throws IOException {
    try (metaInf; rwt) {
      assert true;
    }
  }
}
