/*-
 * #%L
 * marid-test
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
package org.marid.test.logging;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class TestLogHandler extends Handler {

  private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
      .appendValue(ChronoField.HOUR_OF_DAY, 2)
      .appendLiteral(':')
      .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
      .appendLiteral(':')
      .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
      .appendLiteral('.')
      .appendValue(ChronoField.MILLI_OF_SECOND, 3)
      .appendLiteral(' ')
      .toFormatter();

  @Override
  public void publish(LogRecord record) {
    final var out = record.getLevel().intValue() > Level.INFO.intValue() ? System.err : System.out;
    synchronized ((PrintStream) out) {
      FORMATTER.formatTo(record.getInstant().atZone(ZoneId.systemDefault()), out);
      out.append(levelSym(record.getLevel())).append(' ').append(record.getLoggerName()).append(' ');
      out.print(record.getThreadID());
      out.print(' ');
      if (record.getMessage() != null) {
        try {
          out.println(MessageFormat.format(record.getMessage(), record.getParameters()));
        } catch (Throwable x) {
          out.println(record.getMessage());
        }
      }
      if (record.getThrown() != null) {
        record.getThrown().printStackTrace(out);
      }
    }
  }

  @Override
  public void flush() {
    System.out.flush();
    System.err.flush();
  }

  @Override
  public void close() throws SecurityException {
  }

  private char levelSym(Level level) {
    switch (level.intValue()) {
      case 900: return 'W';
      case 1000: return 'E';
      case 800: return 'I';
      case 700: return 'C';
      case 500: return 'D';
      case 400: return 'T';
      case 300: return 'F';
      default: {
        final var name = level.getName();
        return name.isEmpty() ? '-' : name.charAt(0);
      }
    }
  }
}
