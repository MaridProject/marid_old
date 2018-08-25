/*-
 * #%L
 * marid-ide-server
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

import com.fasterxml.jackson.annotation.JsonCreator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class Artifact implements Comparable<Artifact> {

  private final String groupId;
  private final String artifactId;
  private final String version;
  private final String classifier;
  private final String packaging;

  @JsonCreator
  public Artifact(String groupId, String artifactId, String version, String classifier, String packaging) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.classifier = classifier == null ? "" : classifier;
    this.packaging = packaging == null ? "jar" : packaging;
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
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (!(obj instanceof Artifact)) {
      return false;
    } else {
      final Artifact that = (Artifact) obj;
      return Arrays.equals(
          new Object[] {this.groupId, this.artifactId, this.version, this.classifier, this.packaging},
          new Object[] {that.groupId, that.artifactId, that.version, that.classifier, that.packaging}
      );
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, artifactId, version, classifier, packaging);
  }

  @Override
  public int compareTo(@NotNull Artifact o) {
    int c = groupId.compareTo(o.groupId);
    if (c != 0) {
      return c;
    }

    c = artifactId.compareTo(o.artifactId);
    if (c != 0) {
      return c;
    }

    c = version.compareTo(o.version);
    if (c != 0) {
      return c;
    }

    c = classifier.compareTo(o.classifier);
    if (c != 0) {
      return c;
    }

    return packaging.compareTo(o.packaging);
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
