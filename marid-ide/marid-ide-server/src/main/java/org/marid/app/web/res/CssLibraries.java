package org.marid.app.web.res;

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

import org.jetbrains.annotations.NotNull;
import org.marid.html.resource.WebjarResources;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.stream.Stream;

@Component
public class CssLibraries {

  private final String defaultUrl;
  private final LinkedHashMap<String, String> map = new LinkedHashMap<>();

  public CssLibraries(WebjarResources resources) {
    map.put("bootstrap", defaultUrl = resources.url("bootstrap", "css/bootstrap.css"));

    final String[] themes = {
        "cerulean",
        "cosmo",
        "cyborg",
        "darkly",
        "flatly",
        "journal",
        "litera",
        "lumen",
        "lux",
        "materia",
        "minty",
        "pulse",
        "sandstone",
        "simplex",
        "sketchy",
        "slate",
        "solar",
        "spacelab",
        "superhero",
        "united",
        "yeti"
    };

    for (final var theme : themes) {
      map.put(theme, resources.url("bootswatch", theme + "/bootstrap.css"));
    }
  }

  public Stream<String> names() {
    return map.keySet().stream();
  }

  public String getCss(@NotNull String name) {
    return map.getOrDefault(name, defaultUrl);
  }
}
