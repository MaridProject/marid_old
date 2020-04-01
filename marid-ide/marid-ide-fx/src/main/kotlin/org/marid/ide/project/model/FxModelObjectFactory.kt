package org.marid.ide.project.model

import org.marid.model.Initializer
import org.marid.model.Literal
import org.marid.model.ModelObjectFactory

object FxModelObjectFactory : ModelObjectFactory {
  override fun newConstRef() = FxConstRef()
  override fun newCellarConstant() = FxCellarConstant()
  override fun newNull() = FxNull
  override fun newRack() = FxRack()
  override fun newWinery() = FxWinery()
  override fun newCellar() = FxCellar()
  override fun newRef() = FxRef()
  override fun newOutput() = FxOutput()
  override fun newInitializer(): Initializer = FxInitializer()
  override fun newLiteral(): Literal = FxLiteral()
}