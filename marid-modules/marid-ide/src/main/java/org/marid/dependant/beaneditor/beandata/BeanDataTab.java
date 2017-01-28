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

package org.marid.dependant.beaneditor.beandata;

import javafx.scene.control.TabPane;
import org.marid.ide.tabs.IdeTab;
import org.marid.idefx.controls.IdeShapes;
import org.marid.spring.xml.BeanData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static javafx.beans.binding.Bindings.createStringBinding;

/**
 * @author Dmitry Ovchinnikov.
 * @since 0.8
 */
@Component
public class BeanDataTab extends IdeTab {

    @Autowired
    public BeanDataTab(@Qualifier("beanData") TabPane beanDataEditorsTabs, BeanData data) {
        super(beanDataEditorsTabs, createStringBinding(data::getName, data.name), () -> IdeShapes.beanNode(data, 16));
        addNodeObservables(data.observables());
    }
}