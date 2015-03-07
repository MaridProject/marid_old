/*
 * Copyright (C) 2015 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.service.proto.pp;

import org.marid.service.proto.ProtoEvent;
import org.marid.service.proto.ProtoObject;
import org.marid.service.util.EmptyScheduledFuture;
import org.marid.service.util.MapUtil;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

/**
 * @author Dmitry Ovchinnikov
 */
public class PpNode extends ProtoObject {

    protected final TreeMap<String, PpNode> nodeMap = new TreeMap<>();
    protected final Descriptor descriptor;

    protected ScheduledFuture<?> task;

    protected PpNode(@Nonnull ProtoObject object, @Nonnull Object name, @Nonnull Map<String, Object> map) {
        super(object, name, map);
        descriptor = f(map, "descriptor", Descriptor.class, Descriptor.DEFAULT);
        MapUtil.children(map, "nodes").forEach((k, v) -> nodeMap.put(MapUtil.name(k), new PpNode(this, k, v)));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Nonnull
    public PpBus getBus() {
        for (ProtoObject object = parent; object != null; object = object.getParent()) {
            if (object instanceof PpBus) {
                return (PpBus) object;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public PpContext getContext() {
        return getBus().getContext();
    }

    @Override
    public synchronized void start() {
        if (task == null) {
            try {
                final long period = descriptor.period(this);
                final long delay = descriptor.delay(this);
                final boolean realTime = descriptor.realTime(this);
                final Consumer<PpNode> task = descriptor.task(this);
                final Runnable t = () -> task.accept(this);
                if (period > 0L) {
                    if (realTime) {
                        this.task = getContext().timer.scheduleAtFixedRate(t, delay, period, TimeUnit.SECONDS);
                    } else {
                        this.task = getContext().timer.scheduleWithFixedDelay(t, delay, period, TimeUnit.SECONDS);
                    }
                } else {
                    if (delay > 0L) {
                        this.task = getContext().timer.schedule(t, delay, TimeUnit.SECONDS);
                    } else {
                        this.task = EmptyScheduledFuture.getInstance();
                    }
                }
            } catch (Exception x) {
                setChanged();
                notifyObservers(new ProtoEvent(this, "start", x));
            }
            setChanged();
            notifyObservers(new ProtoEvent(this, "start", null));
        }
    }

    @Override
    public synchronized void stop() {
        if (task != null) {
            if (!task.isDone()) {
                task.cancel(descriptor.interruptTask(this));
            }
            try {
                while (!task.isDone()) {
                    LockSupport.parkNanos(1L);
                }
            } finally {
                task = null;
                setChanged();
                notifyObservers(new ProtoEvent(this, "stop", null));
            }
        }
    }

    @Override
    public synchronized boolean isRunning() {
        return task != null && !task.isDone();
    }

    @Override
    public synchronized boolean isStarted() {
        return task != null;
    }

    @Override
    public synchronized boolean isStopped() {
        return task == null;
    }

    @Override
    public void close() {
        stop();
        setChanged();
        notifyObservers(new ProtoEvent(this, "close", null));
    }

    protected interface Descriptor {

        Descriptor DEFAULT = new Descriptor() {
        };

        default boolean interruptTask(PpNode node) {
            return true;
        }

        default long period(PpNode node) {
            return 0L;
        }

        default long delay(PpNode node) {
            return 0L;
        }

        default boolean realTime(PpNode node) {
            return false;
        }

        default Consumer<PpNode> task(PpNode node) {
            return n -> {};
        }
    }
}
