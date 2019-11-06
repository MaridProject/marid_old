package org.marid.project.model;

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

import com.google.inject.Injector;
import org.eclipse.xtend.core.XtendInjectorSingleton;
import org.eclipse.xtend.core.parser.antlr.XtendParser;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.marid.test.io.Resources;

import java.io.StringReader;

@Tag("manual")
class XtendTest {

  private final Injector injector = XtendInjectorSingleton.INJECTOR;

  @Test
  void astTest1() {
    final var parser = injector.getInstance(XtendParser.class);
    final var code = Resources.loadString(getClass().getResource("test1.ext"));
    final var result = parser.parse(new StringReader(code));
    System.out.println(result);
  }
}
