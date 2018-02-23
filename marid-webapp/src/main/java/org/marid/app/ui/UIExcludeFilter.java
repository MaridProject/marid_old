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

package org.marid.app.ui;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

public class UIExcludeFilter implements TypeFilter {

  private static final String PREFIX = UIExcludeFilter.class.getPackage().getName();

  @Override
  public boolean match(@NotNull MetadataReader metadataReader, @NotNull MetadataReaderFactory metadataReaderFactory) {
    return metadataReader.getClassMetadata().getClassName().startsWith(PREFIX);
  }
}
