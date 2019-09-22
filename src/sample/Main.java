package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import javafx.scene.text.*;
import javafx.scene.Group;
import javafx.scene.shape.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.collections.*;
import java.io.*;
import javafx.stage.FileChooser;
public class Main extends Application {



    public void start(Stage stage) throws Exception
    {

        try {


            stage.setTitle("FileChooser");

            FileChooser fil_chooser = new FileChooser();

            Label label = new Label("no files selected");

            Button button = new Button("Select");

            EventHandler<ActionEvent> event =
                    new EventHandler<ActionEvent>() {

                        public void handle(ActionEvent e)
                        {
                            File file = fil_chooser.showOpenDialog(stage);

                            if (file != null) {

                                label.setText(file.getAbsolutePath()
                                        + "  selected");
                            }
                        }
                    };

            button.setOnAction(event);

            VBox vbox = new VBox(30, label, button);

            vbox.setOnDragOver(new EventHandler<DragEvent>() {

                @Override
                public void handle(DragEvent event) {
                    if (event.getGestureSource() != vbox
                            && event.getDragboard().hasFiles()) {
                        /* allow for both copying and moving, whatever user chooses */
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }
                    event.consume();
                }
            });

            vbox.setOnDragDropped(new EventHandler<DragEvent>() {

                @Override
                public void handle(DragEvent event) {
                    Dragboard db = event.getDragboard();

                    boolean success = false;
                    if (db.hasFiles()) {
                        label.setText(db.getFiles().get(0).getAbsolutePath()+ "  selected");
                        success = true;
                    }
                    event.setDropCompleted(success);

                    event.consume();
                }
            });


            vbox.setAlignment(Pos.CENTER);

            Scene scene = new Scene(vbox, 800, 500);

            stage.setScene(scene);

            stage.show();
        }

        catch (Exception e) {

            System.out.println(e.getMessage());
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
