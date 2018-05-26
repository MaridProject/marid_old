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

import com.vaadin.ui.TabSheet.CloseHandler;
import com.vaadin.ui.TabSheet.Tab;
import org.marid.applib.annotation.SpringComponent;
import org.marid.applib.spring.ContextUtils;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringComponent
public class ViewFactory {

  private final MainTabs tabs;
  private final Logger logger;
  private final HashMap<ApplicationContext, LinkedList<GenericApplicationContext>> contexts = new HashMap<>();
  private final LinkedList<CloseHandler> closeListeners = new LinkedList<>();

  public ViewFactory(MainTabs tabs, Logger logger) {
    this.tabs = tabs;
    this.logger = logger;
    this.tabs.setCloseHandler((sheet, component) -> {
      try {
        closeListeners.forEach(l -> l.onTabClose(sheet, component));
      } catch (Exception x) {
        logger.error("Unable to handle tab close", x);
      } finally {
        sheet.removeComponent(component);
      }
    });
  }

  public <T> Set<Tab> show(Class<T> conf, Supplier<T> supplier, GenericApplicationContext parent) {
    final var ctx = ContextUtils.context(parent, c -> {
      c.setId(conf.getName());
      c.setDisplayName(conf.getName());
      c.registerBean(conf, supplier);
      c.refresh();
      c.start();
    });

    final var tabs = ctx.getBeansOfType(Tab.class, false, true).values().stream()
        .filter(tab -> IntStream.range(0, this.tabs.getComponentCount()).anyMatch(i -> this.tabs.getTab(i) == tab))
        .collect(Collectors.toCollection(LinkedHashSet::new));

    if (tabs.isEmpty()) {
      logger.warn("No tabs found in {}", ctx.getId());
      ctx.close();
      return tabs;
    }

    contexts.computeIfAbsent(parent, k -> new LinkedList<>()).add(ctx);

    final CloseHandler closeHandler = (CloseHandler) (sheet, component) -> {
      final var tab = ViewFactory.this.tabs.getTab(component);
      if (tabs.remove(tab) && tabs.isEmpty()) { // the current context is a candidate to be closed
        if (!contexts.containsKey(ctx)) { // let's check whether there are tabs depending on this context
          ctx.close();
        }
      }
    };
    closeListeners.add(closeHandler);
    ctx.addApplicationListener(ContextUtils.closeListener(ctx, event -> {
      closeListeners.remove(closeHandler);
      contexts.computeIfPresent(parent, (k, old) -> old.remove(ctx) && old.isEmpty() ? null : old);
      if (!contexts.containsKey(parent)) {
        parent.close();
      }
    }));

    return tabs;
  }
}
