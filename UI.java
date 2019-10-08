
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import net.sourceforge.tess4j.*;

public class UI extends Application {



    public void start(Stage stage) throws Exception
    {


        String PATH_TO_DATA="D:\\Downloads\\jar_files\\Data";
        String LANGUAGE="hye";


        ITesseract instance = new Tesseract();
        instance.setDatapath(PATH_TO_DATA);
        instance.setLanguage(LANGUAGE);

        final TextArea textArea = new TextArea();


            stage.setTitle("OCReate");

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
                                Image image1 = new Image(file.toURI().toString());
                                try {
                                    Object result = instance.doOCR(file);
                                    textArea.setText(result.toString());
                                } catch (TesseractException exe) {
                                    System.err.println(exe.getMessage());
                                }




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

                        try {
                            Object result = instance.doOCR(db.getFiles().get(0));
                            textArea.setText(result.toString());
                        } catch (TesseractException exe) {
                            System.err.println(exe.getMessage());
                        }


                        success = true;
                    }
                    /* let the source know whether the string was successfully
                     * transferred and used */
                    event.setDropCompleted(success);

                    event.consume();
                }
            });
            vbox.setAlignment(Pos.CENTER);
            vbox.getChildren().add(textArea);
            Scene scene = new Scene(vbox, 800, 500);

            stage.setScene(scene);

            stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
