/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.ui.webide.base;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.widgets.Shell;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Locale;

@Component
@ComponentScan
public class UIConfiguration {

  private final Shell shell;

  @Autowired(required = false)
  public UIConfiguration(Shell shell) {
    this.shell = shell;
  }

  @Bean
  public Locale locale(UISession session) {
    return session.getLocale();
  }

  @Bean
  public CommonProfile userProfile(UISession session) {
    final LinkedHashMap map = (LinkedHashMap) session.getHttpSession().getAttribute(Pac4jConstants.USER_PROFILES);
    return (CommonProfile) map.values().iterator().next();
  }

  @Bean
  public UISession uiSession() {
    return RWT.getUISession();
  }

  public Shell getShell() {
    return shell;
  }
}
