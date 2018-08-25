package org.marid.spring;

/*-
 * #%L
 * marid-spring
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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.support.GenericApplicationContext;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("manual")
class EventTest {

  @Test
  void lambdaListenerTest() {
    final var closeCount = new AtomicInteger();
    final var refreshCount = new AtomicInteger();
    final var startCount = new AtomicInteger();

    try (final var context = new GenericApplicationContext()) {
      for (int i = 0; i < 10; i++) {
        context.addApplicationListener((ContextClosedEvent e) -> closeCount.incrementAndGet());
        context.addApplicationListener((ContextStartedEvent e) -> startCount.incrementAndGet());
        context.addApplicationListener((ContextRefreshedEvent e) -> refreshCount.incrementAndGet());
      }
      context.refresh();
      context.start();
    }

    assertEquals(100, startCount.get());
    assertEquals(100, refreshCount.get());
    assertEquals(100, closeCount.get());
  }
}
