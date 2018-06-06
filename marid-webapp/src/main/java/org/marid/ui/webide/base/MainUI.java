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

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.marid.app.web.MaridServlet;
import org.marid.applib.spring.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

@Push(value = PushMode.AUTOMATIC, transport = Transport.LONG_POLLING)
@Viewport("width=device-width, initial-scale=1")
@Theme("marid")
@Component
@ComponentScan
public class MainUI extends UI {

  @Override
  protected void init(VaadinRequest request) {
    getPage().setTitle("Menu");
    setSizeFull();
  }

  @Autowired
  private void init(MainMenuBar mainMenuBar, MainTabs tabs) {
    final var layout = new VerticalLayout(mainMenuBar, tabs);
    layout.setMargin(false);
    layout.setSpacing(true);
    layout.setExpandRatio(tabs, 1);
    layout.setSizeFull();

    setContent(layout);
  }

  @Override
  public void attach() {
    final var parent = getContext();
    final var child = ContextUtils.context(parent, c -> {
      c.setId("mainUI");
      c.setDisplayName("mainUI");
      c.registerBean("mainUI", MainUI.class, () -> this);
      final var registration = addDetachListener(event -> c.close());
      final var closeListener = ContextUtils.closeListener(c, event -> registration.remove());
      c.addApplicationListener(closeListener);
    });

    super.attach();

    child.refresh();
    child.start();
  }

  @Bean("vaadinSession")
  @Override
  public VaadinSession getSession() {
    return super.getSession();
  }

  private GenericApplicationContext getContext() {
    final var service = (VaadinServletService) getSession().getService();
    final var servlet = (MaridServlet) service.getServlet();
    return servlet.getContext();
  }
}
