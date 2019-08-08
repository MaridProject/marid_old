package org.marid.project;

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

import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marid.profile.IdeProfileContext;
import org.marid.project.ivy.IvyRetriever;
import org.marid.project.ivy.event.IvyLogEvent;
import org.marid.project.ivy.event.IvyProgressEvent;
import org.marid.spring.LoggingPostProcessor;
import org.marid.spring.scope.ResettableScope;
import org.marid.test.spring.TempFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;

import static java.lang.System.Logger.Level.INFO;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("functional")
@ExtendWith({SpringExtension.class})
@ContextConfiguration(initializers = {ProjectLocalArtifactTest.Initializer.class})
class ProjectLocalArtifactTest {

  private static final System.Logger LOGGER = System.getLogger(ProjectLocalArtifactTest.class.getName());
  private static final System.Logger IVY = System.getLogger("ivy");
  private static final System.Logger PROGRESS = System.getLogger("progress");

  @Autowired
  private IvyRetriever ivyRetriever;

  @Value("${implementation.version}")
  private String version;

  @Autowired
  private IdeProject project;

  @Test
  void remoteArtifactFetch() throws IOException, ParseException {
    final var result = ivyRetriever.retrieve(ModuleRevisionId.newInstance("com.amazonaws", "aws-java-sdk-ssm", "1.11.301"));

    assertFalse(result.retrieveReport.getRetrievedFiles().isEmpty());

    for (final var file : result.retrieveReport.getRetrievedFiles()) {
      assertTrue(file.toPath().startsWith(project.getIvyDirectory().resolve("lib")));
      assertTrue(Files.isRegularFile(file.toPath()));
    }
  }

  @Test
  void localArtifactFetch() throws IOException, ParseException {
    LOGGER.log(INFO, "Implementation version: {0}", version);

    final var result = ivyRetriever.retrieve(ModuleRevisionId.newInstance("org.marid", "marid-util", version));

    assertFalse(result.retrieveReport.getRetrievedFiles().isEmpty());

    for (final var file : result.retrieveReport.getRetrievedFiles()) {
      assertTrue(file.toPath().startsWith(project.getIvyDirectory().resolve("lib")));
      assertTrue(Files.isRegularFile(file.toPath()));
    }
  }

  @Configuration
  @PropertySource({"classpath:/marid/meta.properties"})
  @Import({IdeProfileContext.class, IdeProjectContext.class, LoggingPostProcessor.class})
  static class Context {

    @Bean
    public TempFolder ideProfileDirectory() {
      return new TempFolder("ide-test");
    }

    @Bean
    public String ideProjectName() {
      return "testProject";
    }

    @EventListener
    public void onProgress(IvyProgressEvent event) {
      if (!event.message.isEmpty()) {
        PROGRESS.log(INFO, event.message);
      }
    }

    @EventListener
    public void onLog(IvyLogEvent event) {
      if (!event.message.isEmpty()) {
        IVY.log(event.level, event.message);
      }
    }
  }

  static class Initializer implements ApplicationContextInitializer<GenericApplicationContext> {

    @Override
    public void initialize(GenericApplicationContext applicationContext) {
      applicationContext.getDefaultListableBeanFactory().registerScope("ivy", new ResettableScope("test"));
    }
  }
}
