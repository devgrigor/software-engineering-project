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
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 * A class that provides UI functionality and acts as a controller. Receives input from the user and processes it using
 * DataParser. Outputs the result using ExportModule.
 */
public class UI extends Application {

    public File file; //file to convert/output
    TextArea textArea; //area to show the resulting text
    DataParser dataParser; //class that performs an OCR
    FileChooser fileChooser; //fileChooser for importing files
    FileChooser fileSaver; //fileChooser for exporting files
    ExportModule exportModule; //class to export the file in desired format
    ObservableList<String> languages; //List of language names in the dropdown selector
    HashMap<String, String> languageNames = new HashMap<>(); //map of language names and their short versions
    ObservableList<String> downloadableLanguages;
    ComboBox LanguageSelector; //Dropdown selector
    ComboBox LanguageDownloadSelector; //Dropdown selector
    //Image load = new Image("file:UI/Load.png", 96, 72, false, false);
    File build;
    InputStream fileReader;

    Image load;
    ImageView imageView;
    RotateTransition rt;

    Download download= new Download();

    public UI() {


        dataParser = new DataParser();
        textArea = new TextArea();
        fileChooser = new FileChooser();
        fileSaver = new FileChooser();
        exportModule = new ExportModule();
        setUpUI();
        Animation a;

        /**
         * Add the supported output extensions to the file chooser
         */
        fileSaver.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        fileSaver.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
        fileSaver.getExtensionFilters().add(new FileChooser.ExtensionFilter("DOC (*.doc)", "*.doc"));

        String path = (new File("").getAbsolutePath());
        path = path.replaceAll("%20", " ");
        path += "\\TrainData";
        dataParser.setPath(path);



    }


    public void start(Stage stage) throws Exception {

        stage.setTitle("OCReate");

        Label selectLabel = new Label("no files selected");

        Button selectButton = new Button("Select");

        /**
         * eventHandler allowing to select a file (input)
         */
        EventHandler<ActionEvent> select =
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e) {
                        file = fileChooser.showOpenDialog(stage);

                        if (file != null) {

                            selectLabel.setText(file.getAbsolutePath()
                                    + "  selected");

                            /**
                             * Do OCR
                             */
                            parseText();



                        }
                    }
                };

        selectButton.setOnAction(select);

        /**
         * eventHandler allowing to export a file (output)
         */
        Button exportButton = new Button("Export");
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

        /**
         * Button for language download
         */

        Button LanguageDownloadButton = new Button("Download");
        EventHandler<ActionEvent> LanguageDownload =
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e) {

                        if(LanguageDownloadSelector.getValue().toString()!=""){
                            downloadLanguage();
                        }


                    }
                };

        LanguageDownloadButton.setOnAction(LanguageDownload);
        /**
         * put the UI elements in a vertical box, that can also take drag and drop input
         */

        StackPane sp= new StackPane();

        HBox hbox = new HBox(100, LanguageDownloadButton,  LanguageDownloadSelector);
        VBox vbox = new VBox(30, hbox, LanguageSelector, selectLabel, selectButton, exportButton);

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
                    parseText();


                    success = true;
                }

                event.setDropCompleted(success);

                event.consume();
            }
        });

        /**
         * sample alignment and size, will be changed in future iterations
         */

        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(textArea);
        //vbox.getChildren().add(load);
        sp.getChildren().add(vbox);

        sp.getChildren().add(imageView);
        imageView.setVisible(false);
        sp.getChildren().get(sp.getChildren().size()-1).setTranslateY(100);
        Scene scene = new Scene(sp, 800, 500);

        stage.setScene(scene);


        stage.show();

    }

    public void parseText(){
        new Thread( ()->{
            imageView.setVisible(true);
            rt.play();

            new Thread( ()->{
            textArea.setText(dataParser.recognize(file));
                Platform.runLater(()-> textArea.getStyleClass().remove("smallLoading"));
                rt.stop();
                imageView.setVisible(false);

            }).start();
        }).start();
    }


    public void setUpUI(){

            build = new File((getClass().getClassLoader().getResourceAsStream("Load.png").toString()));
        InputStream fileReader = getClass().getResourceAsStream("Load.png");
        try{
            FileUtils.copyInputStreamToFile(fileReader, build);
            fileReader.close();
        } catch (IOException e) {

        }




        load = new Image(build.toURI().toString(), 96, 72, false, false);
        build.delete();
        imageView = new ImageView(load);
        rt = new RotateTransition(Duration.seconds(1), imageView);
        rt.setByAngle(360);
        rt.setCycleCount(Timeline.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);

        setUpLanguageList();


    }

    public void setUpSupportedLanguageList(){
        List<String> results = new ArrayList<String>();


        File[] trainDatas = new File("TrainData").listFiles();


        for (File file : trainDatas) {
            if (file.isFile()) {
                results.add(file.getName().replace(".traineddata", ""));

                //System.out.println(file.getName().replace(".traineddata", ""));
            }
        }

        List<String> results2 = new ArrayList<String>();
        Iterator iterator = results.iterator();
        Iterator iteratorMap;


        while (iterator.hasNext()) {
            iteratorMap = languageNames.entrySet().iterator();
            String s=iterator.next().toString();

            while (iteratorMap.hasNext()) {

                Map.Entry mapNext = (Map.Entry)iteratorMap.next();


                if(mapNext.getValue().toString().equals(s)){
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
    public void setUpLanguageList(){

        build = new File((getClass().getClassLoader().getResourceAsStream("languages.txt").toString()));
        InputStream fileReader = getClass().getResourceAsStream("languages.txt");


        try{
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

        for(int i=0; i<result.length-1; i++){
            languageNames.put(result[i].substring(0, result[i].indexOf("|")), result[i].substring(result[i].indexOf("|")+1, result[i].length()-1));

        }
        setUpSupportedLanguageList();

        List<String> results = new ArrayList<String>();
        results.add("");
        Iterator iterator = languageNames.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry next = (Map.Entry)iterator.next();

            if(!languages.contains(next.getKey().toString())){
                results.add(next.getKey().toString());
            }

        }


        downloadableLanguages= FXCollections.observableArrayList(
                results
        );

        LanguageDownloadSelector= new ComboBox(downloadableLanguages);
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
                        System.out.println("UWUUUUUU");
                    }
                };
        LanguageDownloadSelector.setOnAction(languageDownload);
    }

    public void downloadLanguage(){

        new Thread( ()->{
            try {
                download.download(languageNames.get(LanguageDownloadSelector.getValue() + ""));
                Platform.runLater(() -> {
                    try {
                        languages.add(LanguageDownloadSelector.getValue().toString());
                        String s= LanguageDownloadSelector.getValue().toString();


                        LanguageDownloadSelector.setValue(downloadableLanguages.get(0));

                        downloadableLanguages.remove(s);

                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }


        }).start();




    }
    public static void main(String[] args) {
        launch(args);
    }
}

