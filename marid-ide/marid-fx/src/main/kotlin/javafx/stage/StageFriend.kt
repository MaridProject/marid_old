package javafx.stage

interface StageFriend {
  companion object {
    fun isPrimary(stage: Stage): Boolean {
      return stage.isPrimary
    }
  }
}