package org.marid.ide.child.project.actions

import org.marid.fx.action.Fx
import org.marid.fx.extensions.addOrAppend
import org.marid.ide.child.project.ProjectScanner
import org.marid.ide.project.model.FxCellar
import org.marid.ide.project.model.FxCellarConstant
import org.marid.ide.project.model.FxRack
import org.marid.idelib.Tid
import org.springframework.stereotype.Component

@Component
class ItemActions(private val projectScanner: ProjectScanner) {

  fun constantActions(cellar: FxCellar, index: Int = -1): List<Fx> = projectScanner.constants()
    .groupBy { it.declaringClass.`package` }
    .mapValues { (_, v) -> v.groupBy { it.declaringClass } }
    .map { (p, pels) ->
      Tid.from(p, p.name, "icons/pkg.png").fx.children(pels.map { (c, cels) ->
        Tid.from(c, c.simpleName, "icons/class.png").fx.children(cels.map { m ->
          Tid.from(m, m.name, "icons/const.png").fx {
            cellar.constants.addOrAppend(index, FxCellarConstant()
              .apply { setName(cellar.constantName(m.name)) }
              .apply { setFactory(c.name) }
              .apply { setSelector(m.name) }
            )
          }
        })
      })
    }

  fun rackActions(cellar: FxCellar, index: Int = -1): List<Fx> = projectScanner.racks()
    .groupBy { it.declaringClass.`package` }
    .map { (p, pels) ->
      Tid.from(p, p.name, "icons/pkg.png").fx.children(pels.map { c ->
        Tid.from(c, c.declaringClass.simpleName, "icons/rack.png").fx {
          cellar.racks.addOrAppend(index, FxRack()
            .apply { setName(cellar.rackName(c.declaringClass.simpleName)) }
            .apply { setFactory(c.declaringClass.name) }
          )
        }
      })
    }
}