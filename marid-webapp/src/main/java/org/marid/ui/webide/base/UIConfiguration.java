/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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
