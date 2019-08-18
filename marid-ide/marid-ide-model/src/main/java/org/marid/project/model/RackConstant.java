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

import org.jetbrains.annotations.NotNull;
import org.marid.xml.XmlStreams;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RackConstant extends AbstractEntity {

  private String library;
  private final ArrayList<String> arguments;

  RackConstant(@NotNull String id, @NotNull String name, @NotNull String library, @NotNull String... args) {
    super(id, name);
    this.library = library;
    this.arguments = new ArrayList<>(Arrays.asList(args));
  }

  RackConstant(Element element) {
    super(element);
    this.arguments = XmlStreams.elementsByTag(element, "arg")
        .map(Node::getTextContent)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Override
  public String getTag() {
    return "const";
  }

  public String getLibrary() {
    return library;
  }

  public void setLibrary(String library) {
    this.library = library;
  }

  public ArrayList<String> getArguments() {
    return arguments;
  }
}
