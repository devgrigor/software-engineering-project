import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.controlsfx.control.ToggleSwitch;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

/**
 * A class that provides UI functionality and acts as a controller. Receives input from the user and processes it using
 * DataParser and Preprocessor. Outputs the result using ExportModule. Downloads additional languages using Download.
 */
public class UI extends Application {


    File file; //file to convert/output
    File build; //UI building file

    TextArea textArea; //area to show the resulting text
    StackPane sp; // Z-pane
    DataParser dataParser; //class that performs an OCR
    FileChooser fileChooser; //fileChooser for importing files
    FileChooser fileSaver; //fileChooser for exporting files
    ExportModule exportModule; //class to export the file in desired format
    Download download; //Language downloader
    Preprocessor prep; //Image preprocessor

    ObservableList<String> languages; //List of language names in the dropdown selector
    HashMap<String, String> languageNames = new HashMap<>(); //map of language names and their short versions
    ObservableList<String> downloadableLanguages; //Languages that can be downloaded and used
    String destination;
    String filepath;

    ComboBox LanguageSelector; //Dropdown selector for supported languages
    ComboBox LanguageDownloadSelector; //Dropdown selector for languages available for download

    //Images and their holders
    Image load;
    Image downloadImage;

    ImageView imageView;
    ImageView imageViewDownload;

    //Rotation animations
    RotateTransition rt;
    RotateTransition rtDownload;

    //Buttons
    Button selectButton;
    Button exportButton;
    Button LanguageDownloadButton;
    Button processButton;

    //Preprocessor Switch
    ToggleSwitch toggleSwitch;

    //Lables
    Label selectLabel;
    Label PreprocessingLabel;

    //size of the application window
    Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    double width = screenSize.getWidth() * 2 / 3;
    double height = screenSize.getHeight() * 2 / 3;

    boolean toPreprocess = false;
    boolean didPreprocessing = false;

    public UI() {

        dataParser = new DataParser();
        fileChooser = new FileChooser();
        fileSaver = new FileChooser();
        exportModule = new ExportModule();
        download = new Download();

        /**
         * Add the supported output extensions to the file chooser
         */
        fileSaver.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        fileSaver.getExtensionFilters().add(new FileChooser.ExtensionFilter("DOC (*.doc)", "*.doc"));

        /**
         * locate Traindata folder
         */
        String path = (new File("").getAbsolutePath());
        path = path.replaceAll("%20", " ");
        path += "\\TrainData";
        dataParser.setPath(path);

    }


    public void start(final Stage stage) throws Exception {

        setUpAnimations();
        setUpUI(stage); //set up UI components
        Scene scene = new Scene(sp, width, height); //create new scene
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Reads ui images from resources folder and sets up the animations
     */
    public void setUpAnimations() {

        build = new File((getClass().getClassLoader().getResourceAsStream("Load.png").toString()));
        InputStream fileReader = getClass().getResourceAsStream("Load.png");
        try {
            FileUtils.copyInputStreamToFile(fileReader, build);
            fileReader.close();
        } catch (IOException e) {

        }

        load = new Image(build.toURI().toString(), width / 20, width / 20, false, false);
        build.delete();

        imageView = new ImageView(load);
        rt = new RotateTransition(Duration.seconds(1), imageView);
        rt.setByAngle(360);
        rt.setCycleCount(Timeline.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);


        build = new File((getClass().getClassLoader().getResourceAsStream("download.png").toString()));
        fileReader = getClass().getResourceAsStream("download.png");
        try {
            FileUtils.copyInputStreamToFile(fileReader, build);
            fileReader.close();
        } catch (IOException e) {

        }

        downloadImage = new Image(build.toURI().toString(), width / 40, width / 40, false, false);
        build.delete();

        imageViewDownload = new ImageView(downloadImage);
        imageView.setTranslateY(height / 4);
        rtDownload = new RotateTransition(Duration.seconds(1), imageViewDownload);
        rtDownload.setByAngle(360);
        rtDownload.setCycleCount(Timeline.INDEFINITE);
        rtDownload.setInterpolator(Interpolator.LINEAR);

        imageView.setVisible(false);
        imageViewDownload.setVisible(false);

        setUpLanguageList();

    }

    /**
     * Sets up UI components
     */
    public void setUpUI(Stage stage) {
        stage.setTitle("OCReate");

        textArea = new TextArea();

        selectLabel = new Label("No file selected");
        PreprocessingLabel = new Label("Preprocessing");


        selectButton = new Button("Select");

        /**
         * eventHandler allowing to select a file (input)
         */
        EventHandler<ActionEvent> select =
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e) {
                        file = fileChooser.showOpenDialog(stage);

                        if (file != null) {

                            selectLabel.setText(file.getAbsolutePath()
                                    + " is selected");
                        }
                    }
                };

        selectButton.setOnAction(select);


        processButton = new Button("Process");

        /**
         * eventHandler allowing to parse text from input
         */

        EventHandler<ActionEvent> process =
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e) {
                        if (file != null) {
                            parseText();
                        }
                    }
                };

        processButton.setOnAction(process);


        exportButton = new Button("Export");

        /**
         * eventHandler allowing to export a file (output)
         */

        EventHandler<ActionEvent> export =
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e) {

                        file = fileSaver.showSaveDialog(stage);

                        if (file != null && !textArea.getText().equals("")) {

                            exportModule.exportFile(textArea.getText(), file.getName(),
                                    file.getName().substring(file.getName().lastIndexOf(".") + 1),
                                    file.getAbsolutePath());
                        }
                    }
                };

        exportButton.setOnAction(export);


        LanguageDownloadButton = new Button("Download");

        /**
         * eventHandler allowing to download a language
         */

        EventHandler<ActionEvent> LanguageDownload =
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e) {

                        if (LanguageDownloadSelector.getValue().toString() != "Add a language") {
                            downloadLanguage();
                        }
                    }
                };

        LanguageDownloadButton.setOnAction(LanguageDownload);

        /**
         * Preprocessor toggle switch
         */

        toggleSwitch = new ToggleSwitch();

        toggleSwitch.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue == false) {
                toPreprocess = true;
            } else {
                toPreprocess = false;
            }
        }));

        /**
         * eventHandler for language download selector and language chooser
         */

        LanguageDownloadSelector = new ComboBox(downloadableLanguages);
        LanguageDownloadSelector.setValue(downloadableLanguages.get(0));

        dataParser.setLanguage(languageNames.get(LanguageSelector.getValue() + ""));

        EventHandler<ActionEvent> languageChange =
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e) {
                        dataParser.setLanguage(languageNames.get(LanguageSelector.getValue() + ""));
                    }
                };
        LanguageSelector.setOnAction(languageChange);

        EventHandler<ActionEvent> languageDownload =
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e) {

                    }
                };
        LanguageDownloadSelector.setOnAction(languageDownload);


        HBox Preprocessingbox = new HBox(width / 40, PreprocessingLabel, toggleSwitch);
        HBox hbox = new HBox(width / 30, LanguageDownloadButton, LanguageDownloadSelector, imageViewDownload);
        VBox vbox = new VBox(height / 30, hbox, LanguageSelector, selectLabel, selectButton, Preprocessingbox, processButton, exportButton, textArea);

        vbox.setAlignment(Pos.CENTER);
        hbox.setAlignment(Pos.CENTER);
        Preprocessingbox.setAlignment(Pos.CENTER);

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

                    success = true;
                }

                event.setDropCompleted(success);

                event.consume();
            }
        });

        sp = new StackPane(vbox, imageView);

    }

    /**
     * Sets up language downloader
     */
    public void setUpSupportedLanguageList() {

        List<String> results = new ArrayList<String>();

        File[] trainDatas = new File("TrainData").listFiles();

        for (File file : trainDatas) {
            if (file.isFile()) {
                results.add(file.getName().replace(".traineddata", ""));

            }
        }

        List<String> results2 = new ArrayList<String>();
        Iterator iterator = results.iterator();
        Iterator iteratorMap;

        while (iterator.hasNext()) {
            iteratorMap = languageNames.entrySet().iterator();
            String s = iterator.next().toString();

            while (iteratorMap.hasNext()) {

                Map.Entry mapNext = (Map.Entry) iteratorMap.next();

                if (mapNext.getValue().toString().equals(s)) {
                    results2.add(mapNext.getKey().toString());
                    break;
                }
            }

        }

        languages =
                FXCollections.observableArrayList(
                        results2
                );

        LanguageSelector = new ComboBox(languages);
        LanguageSelector.setValue(languages.get(0));

    }

    /**
     * Sets up language chooser
     */
    public void setUpLanguageList() {

        build = new File((getClass().getClassLoader().getResourceAsStream("languages.txt").toString()));
        InputStream fileReader = getClass().getResourceAsStream("languages.txt");

        try {
            FileUtils.copyInputStreamToFile(fileReader, build);
            fileReader.close();
        } catch (IOException e) {

        }

        String content = null;
        try {
            content = new String(Files.readAllBytes(build.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        build.delete();

        String[] result = content.split("\\n");

        for (int i = 0; i < result.length - 1; i++) {
            languageNames.put(result[i].substring(0, result[i].indexOf("|")), result[i].substring(result[i].indexOf("|") + 1, result[i].length() - 1));

        }
        setUpSupportedLanguageList();

        List<String> results = new ArrayList<String>();
        results.add("Add a language");
        Iterator iterator = languageNames.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry next = (Map.Entry) iterator.next();

            if (!languages.contains(next.getKey().toString())) {
                results.add(next.getKey().toString());
            }

        }

        downloadableLanguages = FXCollections.observableArrayList(
                results
        );

    }

    /**
     * Parses text and does preprocessing if needed
     */
    public void parseText() {
        destination = "";
        filepath = file.getPath();
        if (toPreprocess) {
            prep = new Preprocessor();

            try {
                destination = prep.preprocess(file);
                didPreprocessing = true;
                file = new File(destination);
                System.out.println("Now working with the preprocessed file");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        new Thread(() -> {
            imageView.setVisible(true);
            rt.play();
            new Thread(() -> {
                textArea.setText(dataParser.recognize(file));
                Platform.runLater(() -> textArea.getStyleClass().remove("smallLoading"));
                rt.stop();
                imageView.setVisible(false);
                if (didPreprocessing) {
                    try {
                        prep.deleteTemp(destination);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                file = new File(filepath);
            }).start();
        }).start();

    }

    /**
     * Downloads a language
     */
    public void downloadLanguage() {

        new Thread(() -> {
            imageViewDownload.setVisible(true);
            rtDownload.play();
            new Thread(() -> {
                try {

                    LanguageDownloadButton.setDisable(true);
                    download.download(languageNames.get(LanguageDownloadSelector.getValue() + ""));
                    Platform.runLater(() -> {
                        try {
                            languages.add(LanguageDownloadSelector.getValue().toString());
                            String s = LanguageDownloadSelector.getValue().toString();


                            LanguageDownloadSelector.setValue(downloadableLanguages.get(0));

                            downloadableLanguages.remove(s);
                            LanguageDownloadButton.setDisable(false);
                            imageViewDownload.setVisible(false);
                            rtDownload.stop();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }


            }).start();

        }).start();


    }

    public static void main(String[] args) {
        launch();
    }
}

