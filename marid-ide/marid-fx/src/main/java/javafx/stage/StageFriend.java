package javafx.stage;

public interface StageFriend {

  static boolean isPrimary(Stage stage) {
    return stage.isPrimary();
  }
}
