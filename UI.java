import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;

public class UI extends Application {

    public File file;
    TextArea textArea;
    DataParser dataParser;
    FileChooser fileChooser;
    FileChooser fileSaver;
    ExportModule exportModule;
    ObservableList<String> languages;
    HashMap<String, String> languageNames = new HashMap<>();
    ComboBox LanguageSelector;

    public UI() {


        dataParser = new DataParser();
        textArea = new TextArea();
        fileChooser = new FileChooser();
        fileSaver = new FileChooser();
        exportModule = new ExportModule();

        fileSaver.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        fileSaver.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
        fileSaver.getExtensionFilters().add(new FileChooser.ExtensionFilter("DOC (*.doc)", "*.doc"));

        languages =
                FXCollections.observableArrayList(
                        "English",
                        "Italian",
                        "German"
                );

        LanguageSelector = new ComboBox(languages);
        LanguageSelector.setValue(languages.get(0));

        languageNames.put("English", "eng");
        languageNames.put("Italian", "ita");
        languageNames.put("German", "deu");

        dataParser.setLanguage(languageNames.get(LanguageSelector.getValue() + ""));

        EventHandler<ActionEvent> languageChange =
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e) {
                        dataParser.setLanguage(languageNames.get(LanguageSelector.getValue() + ""));
                    }
                };

        String path = (new File("").getAbsolutePath());
        path = path.replaceAll("%20", " ");
        path += "\\TrainData";

        dataParser.setPath(path);
    }

    public void start(Stage stage) throws Exception {
        stage.setTitle("OCReate");

        Label selectLabel = new Label("no files selected");

        Button selectButton = new Button("Select");

        EventHandler<ActionEvent> select =
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e) {
                        file = fileChooser.showOpenDialog(stage);

                        if (file != null) {

                            selectLabel.setText(file.getAbsolutePath()
                                    + "  selected");

                            textArea.setText(dataParser.recognize(file));

                        }
                    }
                };

        selectButton.setOnAction(select);

        Button exportButton = new Button("Export");
        EventHandler<ActionEvent> export =
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e) {

                        file = fileSaver.showSaveDialog(stage);

                        if (file != null && !textArea.getText().equals("")) {

                            exportModule.exportFile(textArea.getText(), file.getName(), file.getName().substring(file.getName().lastIndexOf(".") + 1),
                                    file.getAbsolutePath());

                        }
                    }
                };

        exportButton.setOnAction(export);


        VBox vbox = new VBox(30, LanguageSelector, selectLabel, selectButton, exportButton);

        vbox.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != vbox
                        && event.getDragboard().hasFiles()) {

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
                    file = db.getFiles().get(0);
                    selectLabel.setText(file.getAbsolutePath() + "  selected");
                    textArea.setText(dataParser.recognize(file));

                    success = true;
                }

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

