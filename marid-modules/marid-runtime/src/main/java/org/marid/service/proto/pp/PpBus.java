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

import org.marid.io.DummyTransceiver;
import org.marid.io.Transceiver;
import org.marid.io.TransceiverAction;
import org.marid.service.proto.ProtoEvent;
import org.marid.service.proto.ProtoObject;
import org.marid.service.util.MapUtil;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;

/**
 * @author Dmitry Ovchinnikov
 */
public class PpBus extends ProtoObject {

    protected final TreeMap<String, PpNode> nodeMap = new TreeMap<>();
    protected final Descriptor descriptor;
    protected final Transceiver transceiver;

    protected PpBus(@Nonnull PpContext context, @Nonnull Object name, @Nonnull Map<String, Object> map) {
        super(context, MapUtil.name(name), map);
        descriptor = f(map, "descriptor", Descriptor.class, Descriptor.DEFAULT);
        transceiver = descriptor.transceiver(this, descriptor.transceiverParams(this));
        MapUtil.children(map, "nodes").forEach((k, v) -> nodeMap.put(MapUtil.name(k), new PpNode(this, k, v)));
    }

    @Override
    protected void init() {
        super.init();
        nodeMap.values().forEach(PpNode::init);
    }

    @Override
    public PpContext getParent() {
        return (PpContext) parent;
    }

    @Override
    public PpContext getContext() {
        return getParent();
    }

    public synchronized <T> T io(TransceiverAction<T> a) throws IOException, TimeoutException, InterruptedException {
        return a.apply(transceiver);
    }

    @Override
    public synchronized void start() {
        try {
            transceiver.open();
        } catch (Exception x) {
            setChanged();
            notifyObservers(new ProtoEvent(this, "start", x));
        }
        nodeMap.values().forEach(PpNode::start);
        setChanged();
        notifyObservers(new ProtoEvent(this, "start", null));
    }

    @Override
    public synchronized void stop() {
        nodeMap.values().forEach(PpNode::stop);
        try {
            transceiver.close();
        } catch (Exception x) {
            setChanged();
            notifyObservers(new ProtoEvent(this, "stop", x));
        }
        notifyObservers(new ProtoEvent(this, "stop", null));
    }

    @Override
    public synchronized boolean isRunning() {
        return nodeMap.values().stream().anyMatch(PpNode::isRunning) || transceiver != null;
    }

    @Override
    public synchronized boolean isStarted() {
        return nodeMap.values().stream().allMatch(PpNode::isStarted);
    }

    @Override
    public synchronized boolean isStopped() {
        return nodeMap.values().stream().noneMatch(PpNode::isRunning);
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

        default Map<String, Object> transceiverParams(PpBus bus) {
            return Collections.emptyMap();
        }

        default Transceiver transceiver(PpBus bus, Map<String, Object> params) {
            return DummyTransceiver.INSTANCE;
        }
    }
}
