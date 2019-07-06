/*-
 * #%L
 * marid-db
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

package org.hsqldb.jdbc;

import org.hsqldb.Database;

import org.jetbrains.annotations.NotNull;
import java.sql.SQLException;

import static java.util.logging.Level.SEVERE;
import static org.marid.logging.Log.log;

/**
 * @author Dmitry Ovchinnikov.
 */
public class JDBCSessionConnection extends JDBCConnection {

  public JDBCSessionConnection(@NotNull Database database, @NotNull String schema) throws SQLException {
    super(database.getSessionManager().newSysSession());
    setSchema(schema);
  }

  @Override
  public synchronized void close() {
    try {
      sessionProxy.close();
    } catch (Throwable x) {
      log(SEVERE, "Unable to close session id={0}", x, sessionProxy.getId());
    } finally {
      sessionProxy = null;
    }
  }
}
