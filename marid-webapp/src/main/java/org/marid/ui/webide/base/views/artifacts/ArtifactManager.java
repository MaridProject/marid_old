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
package org.marid.ui.webide.base.views.artifacts;

import org.marid.applib.repository.Artifact;
import org.marid.ui.webide.base.dao.ArtifactDao;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.TreeSet;

@Component
public class ArtifactManager {

  private final ArtifactDao dao;
  private final TreeSet<Artifact> artifacts;

  public ArtifactManager(ArtifactDao dao) {
    this.dao = dao;
    this.artifacts = new TreeSet<>(dao.loadArtifacts());
  }

  public void addArtifacts(Collection<Artifact> artifacts) {
    if (this.artifacts.addAll(artifacts)) {
    }
  }

  public void save() {
    dao.save(artifacts);
  }
}
