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

package org.marid.dependant.beaneditor.beans;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.marid.ide.project.ProjectProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import static org.marid.l10n.L10n.s;

/**
 * @author Dmitry Ovchinnikov.
 */
@Component
public class BeanEditorTab extends Tab {

    @Autowired
    public BeanEditorTab(ProjectProfile profile, TabPane ideTabPane, TabPane beanEditorTabs, Path beanFilePath) {
        super(s("[%s]: %s", profile, profile.getBeansDirectory().relativize(beanFilePath)), beanEditorTabs);
        getProperties().put("profile", profile);
        getProperties().put("path", beanFilePath);
        ideTabPane.getTabs().add(this);
        ideTabPane.getSelectionModel().select(this);
    }

    @Autowired
    private void listenClose(AnnotationConfigApplicationContext context) {
        setOnClosed(event -> context.close());
    }
}