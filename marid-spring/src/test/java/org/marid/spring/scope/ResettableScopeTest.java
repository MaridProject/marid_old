package org.marid.spring.scope;

/*-
 * #%L
 * marid-spring
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.marid.spring.LoggingPostProcessor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.PreDestroy;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.Logger.Level.INFO;
import static org.junit.jupiter.api.Assertions.*;

@Tag("normal")
@ExtendWith({SpringExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(initializers = {ResettableScopeTest.Initializer.class})
class ResettableScopeTest {

  private static final ResettableScope SCOPE = new ResettableScope("testScope");
  private static final AtomicInteger COUNTER = new AtomicInteger();
  private static final AtomicInteger DEPENDENT_COUNTER = new AtomicInteger();
  private static final System.Logger LOGGER = System.getLogger(ResettableScopeTest.class.getName());

  @Autowired
  private ObjectFactory<Runnable> incrementor;

  @Autowired
  private ObjectFactory<Runnable> dependentIncrementor;

  @Autowired
  private ObjectFactory<ChildDestroyableBean> destroyableBean;

  @Autowired
  private GenericApplicationContext context;

  @Test
  @Order(1)
  void instances() throws ScopeResetException {
    final var oldIncrementor = incrementor.getObject();
    final var oldDependentIncrementor = dependentIncrementor.getObject();

    final var destroyableBean = this.destroyableBean.getObject();

    SCOPE.reset();

    final var newIncrementor = incrementor.getObject();
    final var newDependentIncrementor = dependentIncrementor.getObject();

    assertNotSame(oldIncrementor, newIncrementor);
    assertNotSame(oldDependentIncrementor, newDependentIncrementor);

    assertEquals(List.of("child", "parent"), destroyableBean.log);

    final var incrementor = this.incrementor.getObject();
    final var dependentIncrementor = this.dependentIncrementor.getObject();

    assertSame(newIncrementor, incrementor);
    assertSame(newDependentIncrementor, dependentIncrementor);
  }

  @Test
  @Order(2)
  void event() {
    final var event1 = new Event(this);
    final var event2 = new Event(this);

    LOGGER.log(INFO, "Publishing event1");
    context.publishEvent(event1);
    LOGGER.log(INFO, "Publishing event2");
    context.publishEvent(event2);

    final var destroyableBean = this.destroyableBean.getObject();

    assertEquals(List.of(event1, event2), destroyableBean.events);
  }

  @Configuration
  @Import({LoggingPostProcessor.class})
  static class Context {

    private static final AtomicInteger INCREMENT_BASE = new AtomicInteger();

    @Bean
    @Scope("test")
    Runnable dependentIncrementor() {
      final int base = INCREMENT_BASE.incrementAndGet();
      return () -> DEPENDENT_COUNTER.addAndGet(base);
    }

    @Bean
    @Scope("test")
    Runnable incrementor(Runnable dependentIncrementor) {
      return () -> {
        dependentIncrementor.run();
        COUNTER.incrementAndGet();
      };
    }

    @Bean
    @Scope("test")
    ChildDestroyableBean bean() {
      return new ChildDestroyableBean();
    }
  }

  static class Initializer implements ApplicationContextInitializer<GenericApplicationContext> {

    @Override
    public void initialize(@NonNull GenericApplicationContext applicationContext) {
      applicationContext.getDefaultListableBeanFactory().registerScope("test", SCOPE);
    }
  }

  static class DestroyableBean {

    final LinkedList<String> log = new LinkedList<>();
    final LinkedList<Event> events = new LinkedList<>();

    @PreDestroy
    public void close() {
      log.add("parent");
    }
  }

  static class ChildDestroyableBean extends DestroyableBean {

    @PreDestroy
    public void close2() {
      log.add("child");
    }

    @EventListener
    public void onEvent(Event event) {
      events.add(event);
    }
  }

  static class Event extends ApplicationEvent {

    public Event(Object source) {
      super(source);
    }
  }
}
