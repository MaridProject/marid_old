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

package org.marid.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class MaridLogFormatter extends Formatter {

  private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
      .appendValue(ChronoField.YEAR, 4)
      .appendLiteral('-')
      .appendValue(ChronoField.MONTH_OF_YEAR, 2)
      .appendLiteral('-')
      .appendValue(ChronoField.DAY_OF_MONTH, 2)
      .appendLiteral('T')
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
  public String format(LogRecord record) {
    final var buf = new StringWriter(64);

    try (final var writer = new PrintWriter(buf)) {
      formatter.formatTo(record.getInstant().atZone(ZoneId.systemDefault()), writer);
      writer.append(levelSym(record.getLevel()));
      writer.append(' ');
      writer.print(record.getThreadID());
      writer.print(' ');
      writer.write(record.getLoggerName());
      writer.write(' ');

      writer.println(formatMessage(record));

      if (record.getThrown() != null) {
        record.getThrown().printStackTrace(writer);
      }
    }

    return buf.toString();
  }

  private char levelSym(Level level) {
    switch (level.intValue()) {
      case 300: return 'F';
      case 400: return 'T';
      case 500: return 'D';
      case 700: return 'C';
      case 800: return 'I';
      case 900: return 'W';
      case 1000: return 'E';
      default: {
        final var name = level.getName();
        return name.isEmpty() ? '-' : name.charAt(0);
      }
    }
  }
}
