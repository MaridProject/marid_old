package org.marid.fx;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Tag("manual")
public class TableViewTest {

  @Test
  void test() throws Exception {
    final var latch = new CountDownLatch(1);
    Platform.startup(() -> {
      final var stage = new Stage(StageStyle.DECORATED);
      stage.setOnHidden(e -> latch.countDown());
      final var list = FXCollections.<X>observableArrayList(x -> x.observables);
      list.add(new X());
      list.add(new X());
      final var table = new TableView<>(list);
      table.setEditable(true);

      final var c1 = new TableColumn<X, String>("A");
      c1.setPrefWidth(100);
      c1.setCellValueFactory(p -> Bindings.createStringBinding(() -> p.getValue().a.get(), p.getValue().a, p.getValue().b));
      c1.setCellFactory(p -> new TableCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
          super.updateItem(item, empty);
          if (!empty && item != null) {
            final var b = table.getItems().get(getIndex()).b.get();
            setText(item + " " + b);
          } else {
            setText(null);
          }
        }
      });

      final var c2 = new TableColumn<X, String>("B");
      c2.setPrefWidth(100);
      c2.setCellValueFactory(p -> p.getValue().b);
      c2.setCellFactory(TextFieldTableCell.forTableColumn());
      c2.setEditable(true);

      table.getColumns().addAll(List.of(c1, c2));

      final var scene = new Scene(table, 800, 600);
      stage.setScene(scene);
      stage.show();
    });
    latch.await();
  }

  static class X {
    final SimpleStringProperty a = new SimpleStringProperty("");
    final SimpleStringProperty b = new SimpleStringProperty("");
    final Observable[] observables = {a, b};
  }
}
