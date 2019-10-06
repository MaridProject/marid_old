/*-
 * #%L
 * marid-proto
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

package org.marid.proto.impl;

import org.marid.proto.Proto;
import org.marid.proto.ProtoBus;
import org.marid.proto.ProtoBusTaskRunner;
import org.marid.proto.ProtoDriver;
import org.marid.proto.ProtoRoot;
import org.marid.proto.io.ProtoIO;
import org.marid.runtime.io.function.IOSupplier;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.WARNING;
import static org.marid.logging.Log.log;

/**
 * @author Dmitry Ovchinnikov
 */
public class StdProtoBus extends StdProto implements ProtoBus {

  private final StdProtoRoot root;
  private final IOSupplier<? extends ProtoIO> ioProvider;
  private final Map<String, ProtoDriver> drivers = new LinkedHashMap<>();
  private final long terminationTimeout;

  final ScheduledThreadPoolExecutor scheduler;
  final StdProtoHealth health = new StdProtoHealth();

  volatile ProtoIO io;

  public StdProtoBus(StdProtoRoot root, String id, String name, StdProtoBusProps p) {
    super(id, name);
    this.root = root;
    this.root.getItems().put(id, this);
    this.ioProvider = requireNonNull(p.getIoSupplier(), "IO supplier is null");
    this.scheduler = new ScheduledThreadPoolExecutor(p.getThreadCount(), r -> {
      final String threadName = root.getId() + "/" + id;
      final Thread thread = new Thread(root.getThreadGroup(), r, threadName, p.getStackSize());
      final Logger logger = Logger.getLogger(toString());
      thread.setUncaughtExceptionHandler((t, e) -> log(logger, WARNING, "Uncaught exception in {0}", e, t));
      return thread;
    });
    this.terminationTimeout = p.getTerminationTimeout();
  }

  @Override
  public void close() throws IOException {
    scheduler.shutdown();
    final IOException exception = Proto.close(drivers);
    try {
      scheduler.awaitTermination(terminationTimeout, TimeUnit.MILLISECONDS);
    } catch (Exception x) {
      exception.addSuppressed(x);
    }
    synchronized (this) {
      try {
        if (io != null) {
          io.close();
          io = null;
        }
      } catch (Exception x) {
        exception.addSuppressed(x);
      }
    }
    if (exception.getSuppressed().length > 0) {
      throw exception;
    }
  }

  @Override
  public void reset() {
    synchronized (this) {
      try {
        io.close();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      } finally {
        io = null;
      }
    }
  }

  void init() {
    if (io == null && ioProvider != null) {
      synchronized (this) {
        if (io == null) {
          io = ioProvider.get();
        }
      }
    }
  }

  @Override
  public ProtoRoot getParent() {
    return root;
  }

  @Override
  public Map<String, ProtoDriver> getItems() {
    return drivers;
  }

  @Override
  public ProtoBusTaskRunner<StdProtoBus> getTaskRunner() {
    return new StdProtoBusTaskRunner(this);
  }

  @Override
  public StdProtoHealth getHealth() {
    return health;
  }
}
