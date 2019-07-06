/*-
 * #%L
 * marid-ide-server
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

package org.marid.app.maven;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor;
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.core.retrieve.RetrieveReport;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.resolver.IBiblioResolver;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marid.app.ivy.IvyCommonConfiguration;
import org.marid.app.ivy.IvyLoggerAdapter;
import org.marid.spring.LoggingPostProcessor;
import org.marid.test.FileHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@Tag("manual")
public class RepositoryItemTest {

  @Autowired
  private Ivy ivy;

  @Test
  void testResolve() throws Exception {
    final ResolveOptions resolveOptions = new ResolveOptions();
    resolveOptions.setTransitive(true);
    resolveOptions.setDownload(true);
    resolveOptions.setUseCacheOnly(false);

    final String groupId = "com.amazonaws";
    final String artifactId = "aws-java-sdk-ssm";
    final String version = "1.11.301";

    final ModuleRevisionId id = ModuleRevisionId.newInstance("org", "my", "1.0");
    final DefaultModuleDescriptor md = DefaultModuleDescriptor.newDefaultInstance(id);
    final ModuleRevisionId mr = ModuleRevisionId.newInstance(groupId, artifactId, version);
    final DefaultDependencyDescriptor dd = new DefaultDependencyDescriptor(md, mr, false, false, true);

    dd.addDependencyConfiguration("default", "compile");
    md.addDependency(dd);

    final ResolveReport r = ivy.resolve(md, resolveOptions);
    if (r.hasError()) {
      throw new RuntimeException(r.getAllProblemMessages().toString());
    }

    final ModuleDescriptor m = r.getModuleDescriptor();

    final RetrieveOptions retrieveOptions = new RetrieveOptions()
        .setDestArtifactPattern("lib/[artifact].[type]");

    final RetrieveReport rr = ivy.retrieve(m.getModuleRevisionId(), retrieveOptions);
    System.out.println(rr);
  }

  @Configuration
  @Import({LoggingPostProcessor.class, IvyCommonConfiguration.class})
  public static class Context {

    @Bean
    public FileHolder baseDirectory() throws IOException {
      return new FileHolder(Files.createTempDirectory("temp").toFile());
    }

    @Bean
    public IvySettings settings(FileHolder baseDirectory, IBiblioResolver resolver) {
      final IvySettings settings = new IvySettings();
      settings.setBaseDir(baseDirectory.file);
      settings.setDefaultCache(new File(baseDirectory.file, "cache"));
      settings.addResolver(resolver);
      settings.setDefaultResolver(resolver.getName());
      return settings;
    }

    @Bean
    public Ivy ivy(IvySettings settings) {
      final var ivy = new Ivy();
      final var logger = Logger.getLogger("ivy");
      ivy.getLoggerEngine().setDefaultLogger(new IvyLoggerAdapter(logger));
      ivy.setSettings(settings);
      ivy.bind();
      return ivy;
    }
  }
}
