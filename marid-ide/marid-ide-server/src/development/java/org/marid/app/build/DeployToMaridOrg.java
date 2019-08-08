/*-
 * #%L
 * marid-ide-server
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

package org.marid.app.build;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;

public class DeployToMaridOrg {

  public static void main(String... args) throws Throwable {
    {
      final Process process = new ProcessBuilder(
          "mvn",
          "-DskipTests",
          "-pl", "org.marid:marid-ide-server",
          "-P", "release",
          "-am",
          "clean", "install"
      ).inheritIO().start();
      final int result = process.waitFor();
      if (result != 0) {
        throw new IllegalStateException("Result: " + result);
      }
    }

    {
      final ProtectionDomain protectionDomain = DeployToMaridOrg.class.getProtectionDomain();
      final CodeSource codeSource = protectionDomain.getCodeSource();
      final URL location = codeSource.getLocation();
      final Path path = Paths.get(location.toURI()).getParent().getParent();

      final Process process = new ProcessBuilder("mvn", "wagon:sshexec@stop", "wagon:upload@deploy", "wagon:sshexec@start")
          .inheritIO()
          .directory(path.toFile())
          .start();
      final int result = process.waitFor();
      if (result != 0) {
        throw new IllegalStateException("Result: " + result);
      }
    }

    {
      final Process process = new ProcessBuilder(
          "mvn",
          "-DskipTests",
          "-pl", "org.marid:marid-ide-server",
          "-P", "development",
          "-am",
          "clean", "install"
      ).inheritIO().start();
      final int result = process.waitFor();
      if (result != 0) {
        throw new IllegalStateException("Result: " + result);
      }
    }
  }
}
