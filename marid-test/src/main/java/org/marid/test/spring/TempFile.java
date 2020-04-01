package org.marid.test.spring;

/*-
 * #%L
 * marid-test
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
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
