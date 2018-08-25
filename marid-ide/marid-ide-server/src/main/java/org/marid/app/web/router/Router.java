/*-
 * #%L
 * marid-ide-server
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
package org.marid.app.web.router;

import org.marid.applib.spring.event.HttpSessionDestroyedEvent;
import org.marid.dyn.web.WebContext;
import org.marid.logging.Log;
import org.marid.spring.ContextUtils;
import org.marid.spring.events.ContextClosedListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.WARNING;
import static org.marid.logging.Log.log;

@Component
public class Router {

  private final long idleInterval;
  private final GenericApplicationContext root;
  private final ConcurrentHashMap<String, State> tree = new ConcurrentHashMap<>();

  public Router(@Value("${contextIdleInterval:600}") long idleInterval, GenericApplicationContext root) {
    this.idleInterval = idleInterval;
    this.root = root;
  }

  void route(HttpServletRequest request, HttpServletResponse response) throws Exception {
    final var session = request.getSession();
    final var pathInfo = request.getPathInfo();
    final var path = List.of(pathInfo.substring(1).split("/"));
    final var state = tree.computeIfAbsent(session.getId(), id -> new State(session));
    final var action = state.action(path);

    if (action != null) {
      action.run(request, response);
    } else {
      Log.log(WARNING, "No action found: {0}", request);
      response.sendError(404);
    }
  }

  @EventListener
  public void onSessionDestroyed(HttpSessionDestroyedEvent event) {
    final var state = tree.get(event.getSource().getId());
    if (state != null) {
      state.close();
    }
  }

  @Scheduled(fixedDelay = 1_000L)
  public void update() {
    final long expirationThreshold = System.currentTimeMillis() - idleInterval * 1_000L;
    tree.forEach((k, v) -> v.expire(expirationThreshold));
  }

  private final class State implements AutoCloseable {

    private final GenericApplicationContext context;
    private final ConcurrentHashMap<String, State> children = new ConcurrentHashMap<>();
    private volatile long lastAccess;

    private State(HttpSession session) {
      final var id = session.getId();
      context = ContextUtils.context(root, (r, c) -> {
        c.setId(id);
        c.setDisplayName(id);
        r.registerBean(WebContext.class, () -> new WebContext(session));
        c.addApplicationListener((ContextClosedListener) e -> tree.remove(id));
        c.refresh();
        c.start();
      });
    }

    private State(GenericApplicationContext context) {
      this.context = context;
    }

    private RoutingAction action(List<String> path) {
      switch (path.size()) {
        case 0:
          return null;
        case 1: {
          for (final var e : context.getBeansOfType(RoutingActions.class).entrySet()) {
            final var actions = e.getValue();
            final var action = actions.action(path.get(0));
            if (action != null) {
              touch();
              log(FINE, "Processing {0} by {1}", context.getId() + "/" + path.get(0), e.getKey());
              return action;
            }
          }
          return null;
        }
        default: {
          final var child = children.computeIfAbsent(path.get(0), k -> {
            for (final var e : context.getBeansOfType(RoutingPaths.class).entrySet()) {
              final var paths = e.getValue();
              final var c = paths.get(path.get(0));
              if (c != null) {
                return children.computeIfAbsent(path.get(0), p -> new State(ContextUtils.context(context, (r, ctx) -> {
                  final var id = context.getId() + "/" + path.get(0);
                  ctx.setId(id);
                  ctx.setDisplayName(id);
                  ctx.addApplicationListener((ContextClosedListener) ev -> children.remove(path.get(0)));
                  c.conf.accept(r, ctx);
                  ctx.refresh();
                  ctx.start();
                })));
              }
            }
            return null;
          });
          if (child == null) {
            return null;
          } else {
            return child.action(path.subList(1, path.size()));
          }
        }
      }
    }

    @Override
    public void close() {
      if (context != null) {
        context.close();
      }
    }

    private void touch() {
      lastAccess = System.currentTimeMillis();
    }

    private void expire(long threshold) {
      children.forEach((k, v) -> v.expire(threshold));
      if (lastAccess < threshold && children.isEmpty()) {
        context.close();
      }
    }
  }
}
