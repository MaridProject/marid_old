/*-
 * #%L
 * marid-processors
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

import org.marid.processors.CheckedFunctionalInterfaceProcessor;
import org.marid.processors.GenerateHelperProcessor;

import javax.annotation.processing.Processor;

module marid.processors {

  requires java.compiler;
  requires jdk.compiler;
  requires jdk.unsupported;
  requires jdk.dynalink;

  exports org.marid.processors;

  provides Processor with
      CheckedFunctionalInterfaceProcessor,
      GenerateHelperProcessor;
}
