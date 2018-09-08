package org.marid.dyn.web;

/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Component
public class ThemeManager {

  private static final String THEME_COOKIE = "t";

  public void setTheme(HttpServletRequest request, HttpServletResponse response, String themeId) {
    final var cookie = Arrays.stream(request.getCookies())
        .filter(c -> THEME_COOKIE.equals(c.getName()))
        .findFirst()
        .orElseGet(() -> {
          final var c = new Cookie(THEME_COOKIE, null);
          c.setHttpOnly(true);
          response.addCookie(c);
          return c;
        });
    cookie.setValue(themeId);
  }

  public String getTheme(HttpServletRequest request) {
    return Arrays.stream(request.getCookies())
        .filter(c -> THEME_COOKIE.equals(c.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse("bootstrap");
  }
}