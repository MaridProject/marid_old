/*-
 * #%L
 * marid-ide
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.marid.dependant.beaneditor;

import javafx.scene.control.TreeItem;
import org.marid.beans.IdeBean;
import org.marid.jfx.action.FxAction;

import java.util.Optional;
import java.util.function.Function;

public interface BeanActionProvider extends Function<TreeItem<IdeBean>, FxAction> {

	@Override
	default FxAction apply(TreeItem<IdeBean> ideBeanTreeItem) {
		return Optional.ofNullable(ideBeanTreeItem)
				.flatMap(e -> Optional.ofNullable(e.getValue()))
				.map(this::apply)
				.orElse(null);
	}

	FxAction apply(IdeBean bean);
}
