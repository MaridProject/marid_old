package org.marid.ide.child.project.winery

import javafx.scene.control.TableView
import org.marid.ide.project.model.InitializerWrapper
import org.springframework.stereotype.Component

@Component
class RackInitializerList : TableView<InitializerWrapper>() {
}