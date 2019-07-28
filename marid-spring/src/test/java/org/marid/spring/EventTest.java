package org.marid.spring;

/*-
 * #%L
 * marid-spring
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
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

import org.junit.jupiter.api.*;
import org.marid.spring.events.BroadcastEvent;
import org.marid.spring.events.ForwardingEvent;
import org.marid.spring.events.PropagatedEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("manual")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EventTest {

  @Test
  @Order(1)
  void propagated() {
    final var contexts = contexts();

    contexts.get("ctx0").publishEvent(new PEvent());
    assertEquals(1, contexts.get("ctx0").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx01").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx011").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx012").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx02").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx021").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx022").getBean("propagated", AtomicInteger.class).get());

    contexts.get("ctx01").publishEvent(new PEvent());
    assertEquals(2, contexts.get("ctx0").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx01").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx011").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx012").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx02").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx021").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx022").getBean("propagated", AtomicInteger.class).get());

    contexts.get("ctx02").publishEvent(new PEvent());
    assertEquals(3, contexts.get("ctx0").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx01").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx011").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx012").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx02").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx021").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx022").getBean("propagated", AtomicInteger.class).get());

    contexts.get("ctx011").publishEvent(new PEvent());
    assertEquals(4, contexts.get("ctx0").getBean("propagated", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx01").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx011").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx012").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx02").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx021").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx022").getBean("propagated", AtomicInteger.class).get());

    contexts.get("ctx012").publishEvent(new PEvent());
    assertEquals(5, contexts.get("ctx0").getBean("propagated", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx01").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx011").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx012").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx02").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx021").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx022").getBean("propagated", AtomicInteger.class).get());

    contexts.get("ctx021").publishEvent(new PEvent());
    assertEquals(6, contexts.get("ctx0").getBean("propagated", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx01").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx011").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx012").getBean("propagated", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx02").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx021").getBean("propagated", AtomicInteger.class).get());
    assertEquals(0, contexts.get("ctx022").getBean("propagated", AtomicInteger.class).get());

    contexts.get("ctx022").publishEvent(new PEvent());
    assertEquals(7, contexts.get("ctx0").getBean("propagated", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx01").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx011").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx012").getBean("propagated", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx02").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx021").getBean("propagated", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx022").getBean("propagated", AtomicInteger.class).get());
  }

  @Test
  @Order(2)
  void forwarded() {
    final var contexts = contexts();

    contexts.get("ctx0").publishEvent(new FEvent());
    assertEquals(1, contexts.get("ctx0").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx01").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx011").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx012").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx02").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx021").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx022").getBean("forwarded", AtomicInteger.class).get());

    contexts.get("ctx01").publishEvent(new FEvent());
    assertEquals(1, contexts.get("ctx0").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx01").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx011").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx012").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx02").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx021").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx022").getBean("forwarded", AtomicInteger.class).get());

    contexts.get("ctx02").publishEvent(new FEvent());
    assertEquals(1, contexts.get("ctx0").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx01").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx011").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx012").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx02").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx021").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx022").getBean("forwarded", AtomicInteger.class).get());

    contexts.get("ctx011").publishEvent(new FEvent());
    assertEquals(1, contexts.get("ctx0").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx01").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx011").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx012").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx02").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx021").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx022").getBean("forwarded", AtomicInteger.class).get());

    contexts.get("ctx012").publishEvent(new FEvent());
    assertEquals(1, contexts.get("ctx0").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx01").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx011").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx012").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx02").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx021").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx022").getBean("forwarded", AtomicInteger.class).get());

    contexts.get("ctx021").publishEvent(new FEvent());
    assertEquals(1, contexts.get("ctx0").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx01").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx011").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx012").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx02").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx021").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx022").getBean("forwarded", AtomicInteger.class).get());

    contexts.get("ctx022").publishEvent(new FEvent());
    assertEquals(1, contexts.get("ctx0").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx01").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx011").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx012").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx02").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx021").getBean("forwarded", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx022").getBean("forwarded", AtomicInteger.class).get());
  }

  @Test
  @Order(3)
  void broadcast() {
    final var contexts = contexts();

    contexts.get("ctx0").publishEvent(new BEvent(contexts.get("ctx0")));
    assertEquals(1, contexts.get("ctx0").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx01").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx011").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx012").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx02").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx021").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(1, contexts.get("ctx022").getBean("broadcast", AtomicInteger.class).get());

    contexts.get("ctx01").publishEvent(new BEvent(contexts.get("ctx01")));
    assertEquals(2, contexts.get("ctx0").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx01").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx011").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx012").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx02").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx021").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(2, contexts.get("ctx022").getBean("broadcast", AtomicInteger.class).get());

    contexts.get("ctx02").publishEvent(new BEvent(contexts.get("ctx02")));
    assertEquals(3, contexts.get("ctx0").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx01").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx011").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx012").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx02").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx021").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(3, contexts.get("ctx022").getBean("broadcast", AtomicInteger.class).get());

    contexts.get("ctx011").publishEvent(new BEvent(contexts.get("ctx011")));
    assertEquals(4, contexts.get("ctx0").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(4, contexts.get("ctx01").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(4, contexts.get("ctx011").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(4, contexts.get("ctx012").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(4, contexts.get("ctx02").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(4, contexts.get("ctx021").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(4, contexts.get("ctx022").getBean("broadcast", AtomicInteger.class).get());

    contexts.get("ctx012").publishEvent(new BEvent(contexts.get("ctx012")));
    assertEquals(5, contexts.get("ctx0").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(5, contexts.get("ctx01").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(5, contexts.get("ctx011").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(5, contexts.get("ctx012").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(5, contexts.get("ctx02").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(5, contexts.get("ctx021").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(5, contexts.get("ctx022").getBean("broadcast", AtomicInteger.class).get());

    contexts.get("ctx021").publishEvent(new BEvent(contexts.get("ctx021")));
    assertEquals(6, contexts.get("ctx0").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(6, contexts.get("ctx01").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(6, contexts.get("ctx011").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(6, contexts.get("ctx012").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(6, contexts.get("ctx02").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(6, contexts.get("ctx021").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(6, contexts.get("ctx022").getBean("broadcast", AtomicInteger.class).get());

    contexts.get("ctx022").publishEvent(new BEvent(contexts.get("ctx022")));
    assertEquals(7, contexts.get("ctx0").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(7, contexts.get("ctx01").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(7, contexts.get("ctx011").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(7, contexts.get("ctx012").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(7, contexts.get("ctx02").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(7, contexts.get("ctx021").getBean("broadcast", AtomicInteger.class).get());
    assertEquals(7, contexts.get("ctx022").getBean("broadcast", AtomicInteger.class).get());
  }

  private Map<String, GenericApplicationContext> contexts() {
    final var ctx0 = new GenericApplicationContext();
    ctx0.registerBean("propagated", AtomicInteger.class, (Supplier<AtomicInteger>) AtomicInteger::new);
    ctx0.registerBean("forwarded", AtomicInteger.class, (Supplier<AtomicInteger>) AtomicInteger::new);
    ctx0.registerBean("broadcast", AtomicInteger.class, (Supplier<AtomicInteger>) AtomicInteger::new);
    ctx0.addApplicationListener(event -> increment(ctx0, event));
    ctx0.refresh();

    final var ctx01 = ContextUtils.context(ctx0, (r, c) -> {
      register(r);
      c.addApplicationListener(event -> increment(c, event));
      c.refresh();
    });

    final var ctx011 = ContextUtils.context(ctx01, (r, c) -> {
      register(r);
      c.addApplicationListener(event -> increment(c, event));
      c.refresh();
    });

    final var ctx012 = ContextUtils.context(ctx01, (r, c) -> {
      register(r);
      c.addApplicationListener(event -> increment(c, event));
      c.refresh();
    });

    final var ctx02 = ContextUtils.context(ctx0, (r, c) -> {
      register(r);
      c.addApplicationListener(event -> increment(c, event));
      c.refresh();
    });

    final var ctx021 = ContextUtils.context(ctx02, (r, c) -> {
      register(r);
      c.addApplicationListener(event -> increment(c, event));
      c.refresh();
    });

    final var ctx022 = ContextUtils.context(ctx02, (r, c) -> {
      register(r);
      c.addApplicationListener(event -> increment(c, event));
      c.refresh();
    });

    return Map.of(
        "ctx0", ctx0,
        "ctx01", ctx01,
        "ctx02", ctx02,
        "ctx011", ctx011,
        "ctx012", ctx012,
        "ctx021", ctx021,
        "ctx022", ctx022
    );
  }

  private void register(AnnotatedBeanDefinitionReader r) {
    r.registerBean(AtomicInteger.class, "propagated", AtomicInteger::new);
    r.registerBean(AtomicInteger.class, "forwarded", AtomicInteger::new);
    r.registerBean(AtomicInteger.class, "broadcast", AtomicInteger::new);
  }

  private void increment(GenericApplicationContext context, ApplicationEvent event) {
    final var qualifier = event.getClass().getAnnotation(Qualifier.class);
    if (qualifier != null) {
      context.getBean(qualifier.value(), AtomicInteger.class).incrementAndGet();
    }
  }

  @Qualifier("propagated")
  private class PEvent extends PropagatedEvent<EventTest> {
    private PEvent() {
      super(EventTest.this);
    }
  }

  @Qualifier("forwarded")
  private class FEvent extends ForwardingEvent<EventTest> {
    private FEvent() {
      super(EventTest.this);
    }
  }

  @Qualifier("broadcast")
  private class BEvent extends BroadcastEvent<EventTest> {
    private BEvent(GenericApplicationContext context) {
      super(context, EventTest.this);
    }
  }
}
