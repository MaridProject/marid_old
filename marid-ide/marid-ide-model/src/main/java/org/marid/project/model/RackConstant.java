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

import com.github.javaparser.ast.AccessSpecifier;
import org.jetbrains.annotations.NotNull;
import org.marid.xml.XmlStreams;
import org.marid.xml.XmlUtils;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.github.javaparser.ast.AccessSpecifier.PACKAGE_PRIVATE;
import static com.github.javaparser.ast.AccessSpecifier.PRIVATE;
import static com.github.javaparser.ast.AccessSpecifier.PUBLIC;

public final class RackConstant extends AbstractEntity {

  private AccessType accessType;
  private String library;
  private String name;
  private final ArrayList<LiteralExpression> parameters;

  public RackConstant(@NotNull String library, @NotNull String name, @NotNull List<LiteralExpression> parameters) {
    this.library = library;
    this.name = name;
    this.parameters = new ArrayList<>(parameters);
  }

  public RackConstant(Element element) {
    super(element);
    this.name = element.getAttribute("name");
    this.library = element.getAttribute("library");
    this.parameters = XmlStreams.children(element, Element.class)
        .map(LiteralExpression::new)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public RackConstant(InputSource source) {
    this(element(source));
  }

  @NotNull
  @Override
  public String getTag() {
    return "const";
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("name", name);
    element.setAttribute("library", library);
    element.setAttribute("access", accessType.name().toLowerCase());
    for (final var expression : parameters) {
      XmlUtils.appendTo(expression, element);
    }
  }

  public ArrayList<LiteralExpression> getParameters() {
    return parameters;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLibrary() {
    return library;
  }

  public void setLibrary(String library) {
    this.library = library;
  }

  public AccessType getAccessType() {
    return accessType;
  }

  public void setAccessType(AccessType accessType) {
    this.accessType = accessType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(library, name, parameters);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof RackConstant) {
      final var that = (RackConstant) obj;
      return Objects.equals(this.library, that.library)
          && Objects.equals(this.name, that.name)
          && Objects.equals(this.parameters, that.parameters);
    }
    return false;
  }

  public enum AccessType {

    RACK(PRIVATE),
    CELLAR(PACKAGE_PRIVATE),
    WINERY(PUBLIC);

    public final AccessSpecifier accessSpecifier;

    AccessType(AccessSpecifier accessSpecifier) {
      this.accessSpecifier = accessSpecifier;
    }
  }
}
