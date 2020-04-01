/*-
 * #%L
 * marid-ide-fx
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

package org.marid.ide.main

import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.marid.ide.extensions.bean
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class IdePane(
  tabs: IdeTabs,
  menu: IdeMenuBar,
  status: IdeStatusBar
) : BorderPane(tabs, menu, null, status, null) {

  @Autowired
  private fun initScene(primaryStage: ObjectFactory<Stage>) {
    primaryStage.bean.scene = Scene(this, 1024.0, 768.0);
  }
}
