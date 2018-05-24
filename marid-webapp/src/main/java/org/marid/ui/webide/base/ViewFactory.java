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

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.shared.Registration;
import org.marid.applib.spring.ContextUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

import static org.marid.applib.spring.ContextUtils.closeListener;

@Component
public class ViewFactory implements ViewProvider, ViewChangeListener, AutoCloseable {

  private final Navigator navigator;
  private final Registration viewChangeListenerRegistration;
  private final ConcurrentHashMap<String, GenericApplicationContext> views;
  private final ConcurrentHashMap<ApplicationContext, ConcurrentLinkedQueue<GenericApplicationContext>> contexts;
  private final WeakHashMap<ViewChangeEvent, String> events = new WeakHashMap<>();

  public ViewFactory(Navigator navigator) {
    this.navigator = navigator;
    this.views = new ConcurrentHashMap<>();
    this.contexts = new ConcurrentHashMap<>();
    this.viewChangeListenerRegistration = navigator.addViewChangeListener(this);

    navigator.addProvider(this);
  }

  @Override
  public String getViewName(String viewAndParameters) {
    return views.containsKey(viewAndParameters) ? viewAndParameters : null;
  }

  @Override
  public View getView(String viewName) {
    return Optional.ofNullable(views.get(viewName))
        .filter(c -> c.containsBean(viewName))
        .map(c -> c.getBean(viewName, View.class))
        .orElse(null);
  }

  public <V extends View> void show(String path, Class<V> view, Supplier<V> factory, GenericApplicationContext parent) {
    final var ctx = ContextUtils.context(parent, c -> {
      c.setId(path);
      c.setDisplayName(path);
      c.registerBean(path, view, factory);
      c.refresh();
      c.start();
    });
    contexts.computeIfAbsent(parent, k -> new ConcurrentLinkedQueue<>()).add(ctx);
    views.put(path, ctx);
    ctx.addApplicationListener(closeListener(ctx, event -> {
      views.remove(path);
      contexts.computeIfPresent(parent, (k, old) -> old.remove(ctx) && old.isEmpty() ? null : old);
    }));
  }

  @Override
  public boolean beforeViewChange(ViewChangeEvent event) {
    final var cur = event.getNavigator().getState();
    if (cur != null && views.containsKey(cur)) {
      synchronized (events) {
        events.put(event, cur);
      }
    }
    return true;
  }

  @Override
  public void afterViewChange(ViewChangeEvent event) {
    final String cur;
    synchronized (events) {
      cur = events.remove(event);
    }
    if (cur != null) {
      final var context = views.get(cur);
      if (!contexts.containsKey(context)) {
        context.close();
      }
    }
  }

  @Override
  public void close() {
    navigator.removeProvider(this);
    viewChangeListenerRegistration.remove();
  }
}
