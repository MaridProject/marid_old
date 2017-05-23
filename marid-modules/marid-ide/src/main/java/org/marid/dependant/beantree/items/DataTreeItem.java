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

import javafx.beans.value.ObservableValue;
import org.marid.dependant.beantree.data.ItemTextFactory;
import org.marid.ide.common.IdeShapes;
import org.marid.spring.xml.BeanData;
import org.marid.spring.xml.BeanField;
import org.marid.spring.xml.DElement;
import org.marid.spring.xml.DRef;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Method;

import static org.marid.jfx.beans.ConstantValue.bind;
import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.invokeMethod;

/**
 * @author Dmitry Ovchinnikov
 */
public abstract class DataTreeItem<T extends BeanField> extends AbstractTreeItem<T> {

    public DataTreeItem(T elem) {
        super(elem);
    }

    public abstract ObservableValue<String> nameProperty();

    public abstract ObservableValue<DElement<?>> elementProperty();

    @Override
    public ObservableValue<String> getName() {
        return nameProperty();
    }

    @Autowired
    private void init(@Qualifier("itemText") ObjectProvider<String> itemText) {
        bind(graphic, () -> {
            final DElement<?> element = elem.getData();
            if (element instanceof DRef) {
                return IdeShapes.ref(((DRef) element), 20);
            } else if (element instanceof BeanData) {
                return IdeShapes.beanNode(((BeanData) element), 20);
            } else {
                return null;
            }
        });
        bind(text, () -> {
            final DElement<?> element = elem.getData();
            if (element == null) {
                return null;
            } else {
                final Method method = findMethod(ItemTextFactory.class, "itemText", element.getClass());
                if (method == null) {
                    return null;
                } else {
                    return  (String) invokeMethod(method, null, element);
                }
            }
        });
    }
}
