/*
 * Copyright (c) 2017 Dmitry Ovchinnikov
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

package org.marid.dependant.beantree;

import javafx.beans.binding.Bindings;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import org.marid.dependant.beantree.items.AbstractTreeItem;
import org.marid.dependant.beantree.items.ProjectTreeItem;
import org.marid.ide.common.SpecialActions;
import org.marid.ide.project.ProjectProfile;
import org.marid.jfx.action.FxAction;
import org.marid.jfx.action.MaridActions;
import org.marid.jfx.menu.MaridContextMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static org.marid.jfx.LocalizedStrings.ls;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class BeanTree extends TreeTableView<Object> {

    @Autowired
    public BeanTree(ProjectProfile profile, AutowireCapableBeanFactory beanFactory) {
        super(new ProjectTreeItem(profile));
        setShowRoot(true);
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        beanFactory.initializeBean(getRoot(), null);
        beanFactory.autowireBean(getRoot());
    }

    @Order(1)
    @Autowired
    public void nameColumn() {
        final TreeTableColumn<Object, String> column = new TreeTableColumn<>();
        column.textProperty().bind(ls("Name"));
        column.setCellValueFactory(f -> ((AbstractTreeItem<?>) f.getValue()).getName());
        column.setMinWidth(100);
        column.setPrefWidth(250);
        column.setMaxWidth(500);
        getColumns().add(column);
    }

    @Order(2)
    @Autowired
    public void typeColumn() {
        final TreeTableColumn<Object, String> column = new TreeTableColumn<>();
        column.textProperty().bind(ls("Type"));
        column.setCellValueFactory(f -> ((AbstractTreeItem<?>) f.getValue()).getType());
        column.setMinWidth(100);
        column.setPrefWidth(250);
        column.setMaxWidth(500);
        getColumns().add(column);
    }

    @Order(3)
    @Autowired
    public void valueColumn() {
        final TreeTableColumn<Object, AbstractTreeItem<?>> column = new TreeTableColumn<>();
        column.textProperty().bind(ls("Value"));
        column.setCellValueFactory(f -> Bindings.createObjectBinding(() -> (AbstractTreeItem<?>) f.getValue()));
        column.setCellFactory(f -> new TreeTableCell<Object, AbstractTreeItem<?>>() {
            @Override
            protected void updateItem(AbstractTreeItem<?> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.text());
                    setGraphic(item.graphic());
                }
            }
        });
        column.setMinWidth(300);
        column.setPrefWidth(700);
        column.setMaxWidth(2000);
        getColumns().add(column);
    }

    @Autowired
    private void initRow(SpecialActions specialActions) {
        setRowFactory(v -> {
            final TreeTableRow<Object> row = new TreeTableRow<>();
            row.focusedProperty().addListener((observable, oldValue, newValue) -> {
                final AbstractTreeItem<?> treeItem = (AbstractTreeItem<?>) row.getTreeItem();
                treeItem.actionMap.forEach((key, value) -> {
                    final FxAction action = specialActions.getAction(key);
                    if (action != null) {
                        value.copy(action, newValue);
                    }
                });
            });
            row.setContextMenu(new MaridContextMenu(m -> {
                final AbstractTreeItem<?> treeItem = (AbstractTreeItem<?>) row.getTreeItem();
                m.getItems().clear();
                if (treeItem == null) {
                    return;
                }
                m.getItems().addAll(MaridActions.contextMenu(treeItem.actionMap));
                treeItem.menuConsumers.forEach(c -> c.accept(m.getItems()));
            }));
            row.setLineSpacing(1.0);
            return row;
        });
    }

    @EventListener
    private void onDestroy(ContextClosedEvent event) {
        final GenericApplicationContext context = (GenericApplicationContext) event.getApplicationContext();
        ((ProjectTreeItem) getRoot()).destroy(context.getDefaultListableBeanFactory());
    }
}
