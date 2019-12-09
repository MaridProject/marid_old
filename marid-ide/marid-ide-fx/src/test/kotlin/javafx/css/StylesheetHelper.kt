package javafx.css

import java.io.DataOutputStream

object StylesheetHelper {
  val BSS_VERSION = Stylesheet.BINARY_CSS_VERSION
  fun Stylesheet.write(os: DataOutputStream, s: StyleConverter.StringStore) = this.writeBinary(os, s)
}