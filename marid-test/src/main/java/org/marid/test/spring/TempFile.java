package org.marid.test.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.nio.file.Files;
import java.nio.file.Path;

public class TempFile implements FactoryBean<Path>, InitializingBean, DisposableBean {

  private final String prefix;
  private final String suffix;

  private Path file;

  public TempFile(String prefix, String suffix) {
    this.prefix = prefix;
    this.suffix = suffix;
  }

  @Override
  public Path getObject() {
    return file;
  }

  @Override
  public Class<Path> getObjectType() {
    return Path.class;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    file = Files.createTempFile(prefix, suffix);
  }

  @Override
  public void destroy() throws Exception {
    if (file != null) {
      Files.deleteIfExists(file);
    }
  }
}
