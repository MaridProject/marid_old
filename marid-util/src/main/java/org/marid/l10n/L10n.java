/*-
 * #%L
 * marid-util
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

package org.marid.l10n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.text.MessageFormat;
import java.util.Formatter;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;

/**
 * @author Dmitry Ovchinnikov
 */
public class L10n {

  public static String s(@NotNull @PropertyKey(resourceBundle = "res.strings") String key, Object... ps) {
    return s(Locale.getDefault(), key, ps);
  }

  public static String s(@NotNull Locale locale,
                         @NotNull @PropertyKey(resourceBundle = "res.strings") String key,
                         Object... ps) {
    final StringBuilder builder = new StringBuilder(key.length());
    final Formatter formatter = new Formatter(builder);
    s(locale, key, formatter, ps);
    return builder.toString();
  }

  public static void s(@NotNull Locale locale,
                       @NotNull @PropertyKey(resourceBundle = "res.strings") String key,
                       @NotNull Formatter formatter,
                       Object... ps) {
    final ResourceBundle b = getStringsBundle(locale);
    final String r = b.containsKey(key) ? b.getString(key) : key;
    if (ps == null || ps.length == 0) {
      formatter.format("%s", r);
    } else {
      try {
        formatter.format(b.getLocale(), r, ps);
      } catch (Exception x) {
        formatter.format("!%s", r);
      }
    }
  }

  public static String m(@NotNull @PropertyKey(resourceBundle = "res.messages") String k, Object... v) {
    return m(Locale.getDefault(), k, v);
  }

  public static String m(@NotNull Locale locale,
                         @NotNull @PropertyKey(resourceBundle = "res.messages") String k,
                         Object... v) {
    final StringBuffer buffer = new StringBuffer(k.length());
    m(locale, k, buffer, v);
    return buffer.toString();
  }

  public static void m(@NotNull Locale locale,
                       @NotNull @PropertyKey(resourceBundle = "res.messages") String k,
                       @NotNull StringBuffer buffer,
                       Object... v) {
    final ResourceBundle b = getMessagesBundle(locale);
    final String r = b.containsKey(k) ? b.getString(k) : k;
    if (v == null || v.length == 0) {
      buffer.append(r);
    } else {
      try {
        new MessageFormat(r, b.getLocale()).format(v, buffer, null);
      } catch (Exception x) {
        buffer.append('!').append(r);
      }
    }
  }

  public static ResourceBundle getMessagesBundle(@NotNull Locale locale) {
    return getBundle("res.messages", locale, Thread.currentThread().getContextClassLoader());
  }

  public static ResourceBundle getStringsBundle(@NotNull Locale locale) {
    return getBundle("res.strings", locale, Thread.currentThread().getContextClassLoader());
  }
}
