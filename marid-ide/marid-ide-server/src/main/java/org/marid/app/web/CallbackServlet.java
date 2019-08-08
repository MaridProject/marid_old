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
package org.marid.app.web;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CallbackServlet extends HttpServlet {

  private final Config config;
  private final DefaultCallbackLogic<Void, J2EContext> callbackLogic;

  public CallbackServlet(Config config) {
    this.config = config;
    this.callbackLogic = new DefaultCallbackLogic<>();
  }

  @Override
  protected void doGet(HttpServletRequest q, HttpServletResponse r) {
    callbackLogic.perform(new J2EContext(q, r), config, (code, ctx) -> null, "/index.html", true, false, null, null);
  }
}
