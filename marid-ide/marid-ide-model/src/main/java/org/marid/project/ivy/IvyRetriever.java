package org.marid.project.ivy;

/*-
 * #%L
 * marid-ide-model
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
import java.util.Arrays;
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

      for (final var revisionId : revisionIds) {
        final boolean snapshot = revisionId.getRevision().endsWith("-SNAPSHOT");
        final var dependencyDescriptor = new DefaultDependencyDescriptor(moduleDescriptor, revisionId, false, snapshot, true);
        dependencyDescriptor.addDependencyConfiguration("default", "runtime");
        dependencyDescriptor.addDependencyConfiguration("default", "master");
        moduleDescriptor.addDependency(dependencyDescriptor);
      }

      final ResolveReport resolveReport = ivy.resolve(moduleDescriptor, resolveOptions);
      final RetrieveReport retrieveReport = ivy.retrieve(resolveReport.getModuleDescriptor().getModuleRevisionId(), retrieveOptions);

      return new IvyResult(resolveReport, retrieveReport);
    } finally {
      indexAllocator.free(index);
      ivy.popContext();
    }
  }

  public IvyResult retrieve(ModuleRevisionId... ids) throws IOException, ParseException {
    return retrieve(Arrays.asList(ids));
  }
}
