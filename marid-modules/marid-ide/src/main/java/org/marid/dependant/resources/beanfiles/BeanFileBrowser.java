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

package org.marid.dependant.resources.beanfiles;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.util.Pair;
import org.marid.ide.common.SpecialActions;
import org.marid.ide.project.ProjectManager;
import org.marid.ide.project.ProjectProfile;
import org.marid.spring.annotation.OrderedInit;
import org.marid.spring.xml.BeanFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.marid.jfx.LocalizedStrings.ls;
import static org.marid.jfx.icons.FontIcon.M_DELETE;
import static org.marid.jfx.icons.FontIcon.M_EDIT;
import static org.marid.jfx.icons.FontIcons.glyphIcon;
import static org.marid.l10n.L10n.s;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class BeanFileBrowser extends TableView<Pair<Path, BeanFile>> {

    @Autowired
    public BeanFileBrowser(ProjectProfile profile) {
        super(profile.getBeanFiles().sorted(Comparator.comparing(Pair::getKey)));
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        setTableMenuButtonVisible(true);
    }

    @OrderedInit(1)
    public void fileColumn(ProjectManager projectManager) {
        final TableColumn<Pair<Path, BeanFile>, String> col = new TableColumn<>();
        col.textProperty().bind(ls("File"));
        col.setPrefWidth(600);
        col.setMaxWidth(2000);
        col.setCellValueFactory(param -> {
            final Path path = projectManager.getProfile().getBeansDirectory().relativize(param.getValue().getKey());
            return new SimpleStringProperty(path.toString());
        });
        getColumns().add(col);
    }

    @OrderedInit(2)
    public void dateColumn() {
        final TableColumn<Pair<Path, BeanFile>, String> col = new TableColumn<>();
        col.textProperty().bind(ls("Date"));
        col.setPrefWidth(250);
        col.setMaxWidth(300);
        col.setStyle("-fx-alignment: baseline-right");
        col.setCellValueFactory(param -> {
            final Path path = param.getValue().getKey();
            try {
                final FileTime fileTime = Files.getLastModifiedTime(path);
                final Instant instant = fileTime.toInstant();
                return new SimpleStringProperty(instant.atZone(ZoneId.systemDefault()).format(ISO_LOCAL_DATE_TIME));
            } catch (IOException x) {
                return null;
            }
        });
        getColumns().add(col);
    }

    @OrderedInit(3)
    public void beanCountColumn(ProjectManager projectManager) {
        final TableColumn<Pair<Path, BeanFile>, Integer> col = new TableColumn<>();
        col.textProperty().bind(ls("Bean count"));
        col.setPrefWidth(250);
        col.setMaxWidth(250);
        col.setStyle("-fx-alignment: baseline-right");
        col.setCellValueFactory(param -> {
            final Path path = param.getValue().getKey();
            return new SimpleObjectProperty<>(projectManager.getProfile().getBeanFiles().stream()
                    .filter(e -> e.getKey().startsWith(path))
                    .mapToInt(e -> e.getValue().beans.size())
                    .sum());
        });
        getColumns().add(col);
    }

    @Autowired
    private void initRowFactory(BeanFileBrowserActions actions) {
        setRowFactory(v -> new TableRow<Pair<Path, BeanFile>>() {
            @Override
            protected void updateItem(Pair<Path, BeanFile> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setContextMenu(null);
                } else {
                    final ContextMenu menu = new ContextMenu();
                    {
                        final MenuItem menuItem = new MenuItem(s("Edit..."), glyphIcon(M_EDIT, 16));
                        menuItem.setOnAction(actions::launchBeanEditor);
                        menu.getItems().add(menuItem);
                    }
                    menu.getItems().add(new SeparatorMenuItem());
                    {
                        final MenuItem menuItem = new MenuItem(s("Delete"), glyphIcon(M_DELETE, 16));
                        menuItem.setOnAction(actions::onDelete);
                        menu.getItems().add(menuItem);
                    }
                    setContextMenu(menu);
                }
            }
        });
    }

    @Autowired
    private void init(BeanFileBrowserActions actions, SpecialActions specialActions) {
        specialActions.setEditAction(this, ls("Edit..."), actions::launchBeanEditor);
    }
}