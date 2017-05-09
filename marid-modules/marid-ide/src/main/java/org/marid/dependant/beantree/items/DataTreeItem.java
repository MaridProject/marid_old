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

package org.marid.dependant.beantree.items;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import org.marid.spring.xml.DElement;

import static org.marid.dependant.beantree.items.TreeItemUtils.itemGraphic;
import static org.marid.dependant.beantree.items.TreeItemUtils.itemText;

/**
 * @author Dmitry Ovchinnikov
 */
public abstract class DataTreeItem<T> extends AbstractTreeItem<T> {

    public DataTreeItem(T elem) {
        super(elem);
    }

    public abstract ObservableValue<String> nameProperty();

    public abstract ObservableValue<DElement<?>> elementProperty();

    @Override
    public ObservableValue<String> getName() {
        return nameProperty();
    }

    @Override
    public ObservableValue<Node> valueGraphic() {
        return Bindings.createObjectBinding(() -> itemGraphic(elementProperty().getValue()), elementProperty());
    }

    @Override
    public ObservableValue<String> valueText() {
        return Bindings.createStringBinding(() -> itemText(elementProperty().getValue()), elementProperty());
    }
}
