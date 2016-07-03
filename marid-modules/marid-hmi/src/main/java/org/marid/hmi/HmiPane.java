/*
 * Copyright (c) 2016 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.hmi;

import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.StatusBar;
import org.marid.jfx.panes.Dashboard;

/**
 * @author Dmitry Ovchinnikov
 */
public class HmiPane extends BorderPane implements Dashboard {

    final MenuBar menuBar = new MenuBar();
    final ToolBar toolBar = new ToolBar();
    final StatusBar statusBar = new StatusBar();

    public HmiPane() {
        setTop(new VBox(menuBar, toolBar));
        setBottom(statusBar);
        statusBar.setText("");
    }

    @Override
    public MenuBar getMenuBar() {
        return menuBar;
    }

    @Override
    public ToolBar getToolBar() {
        return toolBar;
    }

    @Override
    public StatusBar getStatusBar() {
        return statusBar;
    }
}