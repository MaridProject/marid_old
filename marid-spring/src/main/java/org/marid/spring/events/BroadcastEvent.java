package org.marid.spring.events;

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

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.support.GenericApplicationContext;

import java.util.LinkedHashSet;

public abstract class BroadcastEvent<S> extends ApplicationEvent {

  private final LinkedHashSet<ApplicationContext> passed = new LinkedHashSet<>();

  public BroadcastEvent(GenericApplicationContext context, S source) {
    super(source);
    passed.add(context);
  }

  @SuppressWarnings("unchecked")
  @Override
  public S getSource() {
    return (S) super.getSource();
  }

  public boolean check(ApplicationContext context) {
    return passed.add(context);
  }
}
