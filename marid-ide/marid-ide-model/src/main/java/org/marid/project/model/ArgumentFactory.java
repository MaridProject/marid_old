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

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

interface ArgumentFactory {

  static Argument argument(Element element) {
    try {
      return new ArgumentLiteral(element);
    } catch (IllegalArgumentException e) {
      switch (element.getTagName()) {
        case "ref-method":
          return new ArgumentRefMethod(element);
        case "ref-field":
          return new ArgumentRefField(element);
        default:
          throw new IllegalArgumentException(element.getTagName());
      }
    }
  }

  static Argument argument(InputSource inputSource) {
    return argument(AbstractEntity.element(inputSource));
  }
}
