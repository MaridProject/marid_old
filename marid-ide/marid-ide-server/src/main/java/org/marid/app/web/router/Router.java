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
import java.lang.ref.Cleaner;
import java.lang.ref.Reference;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.logging.Level.WARNING;

@Component
public class Router {

  private final long idleInterval;
  private final GenericApplicationContext root;
  private final ConcurrentHashMap<String, State> tree = new ConcurrentHashMap<>();
  private final Cleaner cleaner = newCleaner();

  public Router(@Value("${contextIdleInterval:600}") long idleInterval, GenericApplicationContext root) {
    this.idleInterval = idleInterval;
    this.root = root;
  }

  void route(HttpServletRequest request, HttpServletResponse response) throws Exception {
    final var session = request.getSession();
    final var pathInfo = request.getPathInfo();
    final var path = pathInfo.substring(1).split("/");
    final var state = tree.computeIfAbsent(session.getId(), id -> new State(session, () -> tree.remove(id)));

    state.doAction(path, request, response);
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

  private static Cleaner newCleaner() {
    final var threadGroup = new ThreadGroup("router");
    final var threadCounter = new AtomicInteger();
    return Cleaner.create(r -> {
      final var thread = new Thread(threadGroup, r, "cleaner-" + threadCounter.incrementAndGet(), 64L * 1024L, false);
      thread.setDaemon(true);
      thread.setPriority(Thread.MIN_PRIORITY);
      return thread;
    });
  }

  private final class State implements AutoCloseable {

    private final State parent;
    private final GenericApplicationContext context;
    private final Runnable destructor;
    private final ConcurrentHashMap<String, State> children = new ConcurrentHashMap<>();
    private volatile long lastAccess;

    private State(HttpSession session, Runnable destructor) {
      this(null, ContextUtils.context(root, (r, c) -> {
        c.setId(session.getId());
        c.setDisplayName(session.getId());
        r.registerBean(WebContext.class, () -> new WebContext(session));
        c.addApplicationListener((ContextClosedListener) e -> destructor.run());
        c.refresh();
        c.start();
      }), destructor);
    }

    private State(State parent, GenericApplicationContext context, Runnable destructor) {
      this.parent = parent;
      this.context = context;
      this.destructor = destructor;
      cleaner.register(this, context::close);
    }

    private void doAction(String[] path, HttpServletRequest request, HttpServletResponse response) throws Exception {
      switch (path.length) {
        case 0:
          notFound(request, response);
          return;
        case 1: {
          final var action = beans(RoutingActions.class)
              .map(a -> a.action(path[0]))
              .filter(Objects::nonNull)
              .peek(a -> lastAccess = System.currentTimeMillis())
              .findFirst()
              .orElse(null);
          if (action != null) {
            action.run(request, response);
            Reference.reachabilityFence(this);
            Reference.reachabilityFence(parent);
          } else {
            notFound(request, response);
          }
          break;
        }
        default: {
          final var child = children.computeIfAbsent(path[0], k -> beans(RoutingPaths.class)
              .map(paths -> paths.get(k))
              .filter(Objects::nonNull)
              .map(c -> new State(this, ContextUtils.context(context, (r, ctx) -> {
                final var id = context.getId() + "/" + k;
                ctx.setId(id);
                ctx.setDisplayName(id);
                ctx.addApplicationListener((ContextClosedListener) ev -> destructor.run());
                c.configure(r, ctx);
                ctx.refresh();
                ctx.start();
              }), () -> children.remove(k)))
              .findFirst()
              .orElse(null));
          if (child == null) {
            notFound(request, response);
          } else {
            child.doAction(Arrays.copyOfRange(path, 1, path.length, String[].class), request, response);
          }
          break;
        }
      }
    }

    private <T> Stream<T> beans(Class<T> type) {
      return Arrays.stream(context.getDefaultListableBeanFactory().getBeanNamesForType(type))
          .map(name -> context.getDefaultListableBeanFactory().getBean(name, type));
    }

    private void notFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
      Log.log(WARNING, "No action found: {0}", request);
      response.sendError(404);
    }

    @Override
    public void close() {
      destructor.run();
    }

    private void expire(long threshold) {
      children.forEach((k, v) -> v.expire(threshold));
      if (lastAccess < threshold && children.isEmpty()) {
        close();
      }
    }
  }
}
