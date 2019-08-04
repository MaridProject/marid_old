package org.marid.project.ivy;

/*-
 * #%L
 * marid-ide-model
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
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

import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor;
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.core.retrieve.RetrieveReport;
import org.marid.misc.FreeIndexAllocator;
import org.marid.project.ivy.model.IvyResult;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Component
public class IvyRetriever {

  private final ObjectFactory<Ivy> ivy;
  private final FreeIndexAllocator indexAllocator = new FreeIndexAllocator();
  private final ResolveOptions resolveOptions;
  private final RetrieveOptions retrieveOptions;

  public IvyRetriever(ObjectFactory<Ivy> ivy, ResolveOptions resolveOptions, RetrieveOptions retrieveOptions) {
    this.ivy = ivy;
    this.resolveOptions = resolveOptions;
    this.retrieveOptions = retrieveOptions;
  }

  public synchronized IvyResult retrieve(List<ModuleRevisionId> revisionIds) throws IOException, ParseException {
    final var ivy = this.ivy.getObject();
    final int index = indexAllocator.freeIndex();
    ivy.pushContext();
    try {
      final var projectModuleRevisionId = ModuleRevisionId.newInstance("org.marid.dynamic", "project" + index, "1.0");
      final var moduleDescriptor = DefaultModuleDescriptor.newDefaultInstance(projectModuleRevisionId);

      revisionIds.forEach(revisionId -> {
        final boolean snapshot = revisionId.getRevision().endsWith("-SNAPSHOT");
        final var dependencyDescriptor = new DefaultDependencyDescriptor(moduleDescriptor, revisionId, false, snapshot, true);
        dependencyDescriptor.addDependencyConfiguration("default", "runtime");
        dependencyDescriptor.addDependencyConfiguration("default", "master");
        moduleDescriptor.addDependency(dependencyDescriptor);
      });

      final ResolveReport resolveReport = ivy.resolve(moduleDescriptor, resolveOptions);
      final RetrieveReport retrieveReport = ivy.retrieve(resolveReport.getModuleDescriptor().getModuleRevisionId(), retrieveOptions);

      return new IvyResult(resolveReport, retrieveReport);
    } finally {
      indexAllocator.free(index);
      ivy.popContext();
    }
  }
}
