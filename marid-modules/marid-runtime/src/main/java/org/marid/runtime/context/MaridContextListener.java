/*-
 * #%L
 * marid-runtime
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
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

package org.marid.runtime.context;

import org.marid.runtime.beans.BeanEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EventListener;
import java.util.function.Consumer;

/**
 * @author Dmitry Ovchinnikov
 */
public interface MaridContextListener extends EventListener, Comparable<MaridContextListener> {

    void bootstrap(MaridRuntime runtime);

    void onEvent(@Nonnull BeanEvent event);

    void onInitialize(@Nonnull String name, @Nullable Object bean);

    void onDestroy(@Nonnull String name, @Nullable Object bean, @Nonnull Consumer<Throwable> throwableConsumer);

    void onStart();

    void onStop();

    void onFail();

    int getOrder();

    @Override
    default int compareTo(@Nonnull MaridContextListener o) {
        return Integer.compare(getOrder(), o.getOrder());
    }
}
