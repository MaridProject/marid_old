/*-
 * #%L
 * marid-util
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

package org.marid.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.StreamCorruptedException;
import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Dmitry Ovchinnikov
 */
public class ProcessManager implements Closeable {

  private final Process process;
  private final long timeout;
  private final TimeUnit timeUnit;
  private final InputStream out;
  private final InputStream err;
  private final Thread outConsumer;
  private final Thread errConsumer;

  public ProcessManager(Process process,
                        Consumer<String> outLineConsumer,
                        Consumer<String> errLineConsumer,
                        int bufferSize,
                        long timeout,
                        TimeUnit timeUnit) {
    this.process = process;
    this.timeout = timeout;
    this.timeUnit = timeUnit;
    this.out = process.getInputStream();
    this.err = process.getErrorStream();
    this.outConsumer = new ConsumerThread(this + "(out)", out, bufferSize, outLineConsumer);
    this.errConsumer = new ConsumerThread(this + "(err)", err, bufferSize, errLineConsumer);
    outConsumer.start();
    errConsumer.start();
  }

  public ProcessManager(Process process, Consumer<String> out, Consumer<String> err) {
    this(process, out, err, 65536, 1L, TimeUnit.SECONDS);
  }

  private void awaitThreads(IOException iox) throws IOException {
    final IOException ex = iox != null ? iox : new IOException();
    try (final InputStream e = err; final InputStream i = out) {
      assert e != null;
      assert i != null;
      try {
        errConsumer.join();
        outConsumer.join();
      } catch (InterruptedException ix) {
        ex.addSuppressed(ix);
      }
    } catch (Exception x) {
      ex.addSuppressed(x);
    }
    if (ex.getMessage() != null || ex.getSuppressed().length > 0) {
      throw ex;
    }
  }

  public int waitFor() throws InterruptedException {
    return process.waitFor();
  }

  @Override
  public String toString() {
    return "pid(" + process.pid() + ")";
  }

  @Override
  public void close() throws IOException {
    if (process.isAlive()) {
      process.destroy();
      try {
        if (!process.waitFor(timeout, timeUnit) || !process.destroyForcibly().waitFor(timeout, timeUnit)) {
          awaitThreads(new StreamCorruptedException(toString()));
        }
      } catch (InterruptedException x) {
        awaitThreads(new InterruptedIOException(process.toString()));
      }
    } else {
      awaitThreads(null);
    }
  }

  private static class ConsumerThread extends Thread {

    private final InputStream inputStream;
    private final Consumer<String> lineConsumer;
    private final int bufferSize;

    private ConsumerThread(String name, InputStream inputStream, int bufferSize, Consumer<String> lineConsumer) {
      super(null, null, name, 96L * 1024L);
      this.inputStream = inputStream;
      this.lineConsumer = lineConsumer;
      this.bufferSize = bufferSize;
    }

    @Override
    public void run() {
      try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), bufferSize)) {
        while (true) {
          final String line = reader.readLine();
          if (line == null) {
            break;
          }
          lineConsumer.accept(line);
        }
      } catch (IOException x) {
        throw new UncheckedIOException("Unexpected exception: " + getName(), x);
      }
    }
  }
}
