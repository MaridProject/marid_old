package org.marid.runtime.internal;

/*-
 * #%L
 * marid-runtime
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.marid.io.MaridFiles;
import org.marid.io.Xmls;
import org.marid.io.function.IOSupplier;
import org.marid.runtime.model.Winery;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class WineryRuntime extends LinkerSupport implements AutoCloseable {

  private final Thread thread;
  private final LinkedTransferQueue<Command> queue = new LinkedTransferQueue<>();
  private final LinkedHashMap<String, CellarRuntime> cellars;
  private final AutoCloseable destroyAction;

  final URLClassLoader classLoader;
  final Winery winery;
  final ArrayList<Map.Entry<String, String>> racks;

  private volatile State state = State.NEW;
  private volatile Throwable startError;
  private volatile Throwable destroyError;

  private WineryRuntime(WineryParams params) {
    this.cellars = new LinkedHashMap<>(params.winery.getCellars().size());
    this.classLoader = params.classLoader;
    this.destroyAction = params.destroyAction;
    this.winery = params.winery;
    this.racks = new ArrayList<>(winery.getCellars().stream().mapToInt(c -> c.getRacks().size()).sum());
    this.thread = new Thread(null, () -> {
      try {
        while (!Thread.currentThread().isInterrupted()) {
          final var task = queue.poll(10L, TimeUnit.MILLISECONDS);
          if (task == null) {
            continue;
          }
          switch (task) {
            case START:
              try {
                run();
              } catch (Throwable e) {
                startError = e;
              }
              break;
            case STOP:
              try {
                destroy();
              } catch (Throwable e) {
                destroyError = e;
              }
              break;
          }
        }
      } catch (InterruptedException e) {
        // exit thread
      } finally {
        destroy();
      }
    }, winery.getName(), 96L << 10);
  }

  public WineryRuntime(URL zipFile, List<String> args) {
    this(new WineryParams(zipFile, args));
  }

  @TestOnly
  public WineryRuntime(ClassLoader classLoader, Winery winery, AutoCloseable destroyAction) {
    this(new WineryParams(new URLClassLoader(new URL[0], classLoader), winery, destroyAction));
  }

  @TestOnly
  public WineryRuntime(Winery winery, AutoCloseable destroyAction) {
    this(Thread.currentThread().getContextClassLoader(), winery, destroyAction);
  }

  @TestOnly
  public WineryRuntime(Winery winery) {
    this(winery, () -> {});
  }

  private static void unpack(Path deployment, ZipInputStream zipInputStream) throws IOException {
    for (var e = zipInputStream.getNextEntry(); e != null; e = zipInputStream.getNextEntry()) {
      try {
        final var target = deployment.resolve(e.getName());

        if (!target.startsWith(deployment)) {
          throw new StreamCorruptedException("Invalid entry: " + e.getName());
        }

        if (target.equals(deployment)) {
          continue;
        }

        if (e.isDirectory()) {
          Files.createDirectory(target);
        } else {
          Files.copy(zipInputStream, target);
        }
        Files.setLastModifiedTime(target, e.getLastModifiedTime());
      } finally {
        zipInputStream.closeEntry();
      }
    }
  }

  private static void validate(Path resources, Path deps, Path classes) throws IOException {
    Files.createDirectories(resources);
    Files.createDirectories(deps);
    if (!Files.isDirectory(classes)) {
      throw new FileNotFoundException(classes.toString());
    }
  }

  private static void initialize(Path deployment, List<String> args) throws IOException {
    final var propsFile = deployment.resolve("system.properties");
    if (Files.isRegularFile(propsFile)) {
      final var props = new Properties();
      try (final var reader = Files.newBufferedReader(propsFile, UTF_8)) {
        props.load(reader);
      }
      props.forEach(System.getProperties()::putIfAbsent);
    }
    for (final var arg : args) {
      if (arg.startsWith("--") && arg.contains("=")) {
        final var eqi = arg.indexOf('=');
        final var key = arg.substring(2, eqi);
        final var val = arg.substring(eqi + 1).trim();
        System.getProperties().putIfAbsent(key, val);
      }
    }
  }

  public @NotNull String getId() {
    return winery.getName();
  }

  public @NotNull CellarRuntime getCellar(@NotNull String name) {
    return Objects.requireNonNull(cellars.get(name), () -> "No such cellar in " + winery.getName() + ": " + name);
  }

  public @NotNull Set<@NotNull String> getCellarNames() {
    return Collections.unmodifiableSet(cellars.keySet());
  }

  public void start() {
    switch (thread.getState()) {
      case NEW:
        thread.start();
        break;
      case TERMINATED:
        throw new IllegalStateException();
    }
    switch (state) {
      case STARTING:
      case RUNNING:
      case TERMINATING:
        return;
      case NEW:
      case TERMINATED:
        queue.add(Command.START);
        while (state != State.RUNNING && state != State.TERMINATED) {
          Thread.onSpinWait();
        }
        if (startError != null) {
          try {
            throw startError;
          } catch (RuntimeException | Error e) {
            throw e;
          } catch (Throwable e) {
            throw new IllegalStateException(e);
          } finally {
            startError = null;
          }
        }
        break;
    }
  }

  private void run() {
    if (state == State.STARTING || state == State.RUNNING) {
      return;
    }

    state = State.STARTING;
    Thread.currentThread().setContextClassLoader(classLoader);

    try {
      winery.getCellars().forEach(c -> cellars.put(c.getName(), new CellarRuntime(this, c)));
      cellars.forEach((name, c) -> c.cellar.getConstants().forEach(e -> c.getOrCreateConst(e, new LinkedHashSet<>())));
      cellars.forEach((name, c) -> c.cellar.getRacks().forEach(e -> c.getOrCreateRack(e, new LinkedHashSet<>())));

      state = State.RUNNING;
    } catch (Throwable e) {
      try {
        destroy();
      } catch (Throwable x) {
        e.addSuppressed(x);
      }
      throw e;
    }
  }

  private void destroy() {
    if (state != State.RUNNING && state != State.STARTING) {
      return;
    }
    state = State.TERMINATING;
    final var exception = new IllegalStateException("Unable to close winery " + getId());

    for (int i = racks.size() - 1; i >= 0; i--) {
      final var rackEntry = racks.get(i);
      final var cellar = getCellar(rackEntry.getKey());
      final var rack = cellar.getRack(rackEntry.getValue());
      try {
        rack.close();
      } catch (Throwable e) {
        exception.addSuppressed(e);
      }
    }
    racks.clear();

    cellars.forEach((name, c) -> c.close());
    cellars.clear();

    if (classLoader != null) {
      try {
        classLoader.close();
      } catch (Throwable e) {
        exception.addSuppressed(e);
      }
    }

    try {
      destroyAction.close();
    } catch (Throwable e) {
      exception.addSuppressed(e);
    }

    state = State.TERMINATED;

    if (exception.getSuppressed().length > 0) {
      throw exception;
    }
  }

  @Override
  public void close() throws Exception {
    queue.add(Command.STOP);
    while (state != State.TERMINATED) {
      Thread.onSpinWait();
    }
    if (destroyError != null) {
      try {
        throw destroyError;
      } catch (Exception | Error e) {
        throw e;
      } catch (Throwable e) {
        throw new IllegalStateException(e);
      } finally {
        destroyError = null;
      }
    }
  }

  @Override
  public @NotNull String toString() {
    return getId();
  }

  public enum State {NEW, STARTING, RUNNING, TERMINATING, TERMINATED}
  public enum Command {START, STOP}

  private static final class WineryParams {

    private final URLClassLoader classLoader;
    private final Winery winery;
    private final AutoCloseable destroyAction;

    private WineryParams(URL zipFile, List<String> args) {
      final var deployment = IOSupplier.of(() -> Files.createTempDirectory("marid")).get();
      destroyAction = () -> {
        final var p = this;
        if (p.classLoader != null) {
          try {
            p.classLoader.close();
          } finally {
            MaridFiles.deleteRecursively(deployment);
          }
        } else {
          MaridFiles.deleteRecursively(deployment);
        }
      };
      try {
        try (final var is = new ZipInputStream(zipFile.openStream(), UTF_8)) {
          unpack(deployment, is);
        }

        final var classes = deployment.resolve("classes");
        final var resources = deployment.resolve("resources");
        final var deps = deployment.resolve("deps");
        final var winery = deployment.resolve("winery.xml");

        this.winery = Xmls.read(winery, Winery::new);

        validate(resources, deps, classes);
        initialize(deployment, args);

        classLoader = classLoader(classes, resources, deps);
      } catch (Throwable e) {
        try {
          destroyAction.close();
        } catch (Throwable x) {
          e.addSuppressed(x);
        }
        throw new IllegalStateException(e);
      }
    }

    private WineryParams(URLClassLoader classLoader, Winery winery, AutoCloseable destroyAction) {
      this.classLoader = classLoader;
      this.winery = winery;
      this.destroyAction = destroyAction;
    }

    private URLClassLoader classLoader(Path classes, Path resources, Path deps) throws IOException {
      final var urls = new ArrayList<URL>();
      urls.add(classes.toUri().toURL());
      urls.add(resources.toUri().toURL());
      try (final var dirStream = Files.newDirectoryStream(deps, "*.jar")) {
        for (final var path : dirStream) {
          urls.add(path.toUri().toURL());
        }
      }
      return new URLClassLoader(urls.toArray(URL[]::new), Thread.currentThread().getContextClassLoader());
    }
  }
}
