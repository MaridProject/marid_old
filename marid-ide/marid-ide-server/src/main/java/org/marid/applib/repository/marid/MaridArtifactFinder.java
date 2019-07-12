/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.applib.repository.marid;

import org.marid.applib.repository.Artifact;
import org.marid.applib.repository.ArtifactFinder;

import java.util.List;

public class MaridArtifactFinder implements ArtifactFinder {

  @Override
  public List<Artifact> find(String groupPattern, String artifactPattern, String classPattern) {
    return List.of();
  }
}
