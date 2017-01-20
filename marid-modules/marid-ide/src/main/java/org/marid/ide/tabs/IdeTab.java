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

package org.marid.ide.tabs;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import org.marid.jfx.props.Props;
import org.springframework.context.support.GenericApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import static org.marid.jfx.LocalizedStrings.ls;

/**
 * @author Dmitry Ovchinnikov.
 */
public class IdeTab extends Tab {

    @Resource
    protected transient IdeTabPane ideTabPane;

    @Resource
    protected transient GenericApplicationContext context;

    public IdeTab(Node content, String text, Object...args) {
        super(null, content);
        textProperty().bind(ls(text, args));
    }

    @PostConstruct
    private void init() {
        final int index = ideTabPane.getTabs().indexOf(this);
        if (index >= 0) {
            ideTabPane.getTabs().remove(index);
            ideTabPane.getTabs().add(index, this);
        } else {
            ideTabPane.getTabs().add(this);
        }
        ideTabPane.getSelectionModel().select(this);
        Props.addHandler(onClosedProperty(), event -> context.close());
    }

    @PreDestroy
    private void destroy() {
        ideTabPane.getTabs().remove(this);
    }
}
