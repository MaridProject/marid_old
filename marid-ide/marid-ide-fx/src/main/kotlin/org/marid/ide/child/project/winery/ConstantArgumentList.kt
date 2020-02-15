package org.marid.ide.child.project.winery

import javafx.scene.control.ListView
import org.marid.ide.project.model.FxConstantArgument
import org.springframework.stereotype.Component

@Component
class ConstantArgumentList : ListView<FxConstantArgument>() {
}