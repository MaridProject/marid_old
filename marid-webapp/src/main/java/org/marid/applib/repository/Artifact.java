/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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
package org.marid.applib.repository;

import java.util.Map;

public class Artifact {

  private final String groupId;
  private final String artifactId;
  private final String version;
  private final String classifier;
  private final String packaging;

  public Artifact(String groupId, String artifactId, String version, String classifier, String packaging) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.classifier = classifier;
    this.packaging = packaging;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getVersion() {
    return version;
  }

  public String getClassifier() {
    return classifier;
  }

  public String getPackaging() {
    return packaging;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + Map.of(
        "groupId", groupId,
        "artifactId", artifactId,
        "version", version,
        "classifier", classifier,
        "packaging", packaging
    );
  }
}
