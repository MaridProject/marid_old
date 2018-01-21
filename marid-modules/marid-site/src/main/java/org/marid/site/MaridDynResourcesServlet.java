/*-
 * #%L
 * marid-site
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

package org.marid.site;

import org.marid.site.annotation.DynResLiteral;
import org.marid.site.resources.DynResource;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "dynMarid", urlPatterns = "/dyn/*")
public class MaridDynResourcesServlet extends HttpServlet {

  @Inject
  private Instance<DynResource> dynResources;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    final String name = req.getRequestURI().substring(5);
    for (final DynResource resource : dynResources.select(new DynResLiteral(name))) {
      if (resource != null) {
        resp.setStatus(HttpServletResponse.SC_OK);
        resource.doGet(req, resp);
        return;
      }
    }
    resp.sendError(404, "Not found: " + req.getRequestURI());
  }
}