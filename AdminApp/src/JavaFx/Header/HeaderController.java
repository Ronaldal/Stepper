package JavaFx.Header;
import JavaFx.AppController;
import Requester.fileupload.FileUploadImpl;
import Utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

import static AdminUtils.AdminUtils.HTTP_CLIENT;


public class HeaderController {
    private AppController mainController;
    @FXML private VBox headerComponent;

    @FXML private Button loadButton;

    @FXML private Label filePathLabel;

    @FXML
    public void initialize() {
        FileUploadImpl fileUpload = new FileUploadImpl();
        if(Boolean.TRUE.equals(Utils.runSync(fileUpload.isStepperIn(), Boolean.class, HTTP_CLIENT)))
            updateFilePathLabel(Utils.runSync(fileUpload.getFilePath(), String.class, HTTP_CLIENT));

    }
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
    @FXML
    private void handleLoadButtonClick() throws IOException, InterruptedException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        Stage stage = (Stage) loadButton.getScene().getWindow();
        // Show the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String filePath = selectedFile.getPath();
            mainController.loadFile(filePath,selectedFile);
        }

    }

    public void updateFilePathLabel(String filePath) {
        filePathLabel.setText(filePath);
    }
//    @FXML
//    void classicSytleButton(ActionEvent event) {
//        BorderPane root = (BorderPane) classicSytleButton.getScene().getRoot();
//        ObservableList<String> stylesheets = root.getStylesheets();;
//        stylesheets.clear();
//
//        // Add the path to the new stylesheet
//        String cssPath = getClass().getResource("/JavaFx/Body/resource/classic-style.css").toExternalForm();
//        stylesheets.add(cssPath);
//    }
//
//    @FXML
//    void greenSytleButton(ActionEvent event) {
//        BorderPane root = (BorderPane) greenSytle.getScene().getRoot();
//        ObservableList<String> stylesheets = root.getStylesheets();;
//        stylesheets.clear();
//
//        // Add the path to the new stylesheet
//        String cssPath = getClass().getResource("/JavaFx/Body/resource/water-melon-style.css").toExternalForm();
//        stylesheets.add(cssPath);
//    }
//
//    @FXML
//    void seaSytleButton(ActionEvent event) {
//        BorderPane root = (BorderPane) seaSytle.getScene().getRoot();
//        ObservableList<String> stylesheets = root.getStylesheets();;
//        stylesheets.clear();
//
//        // Add the path to the new stylesheet
//        String cssPath = getClass().getResource("/JavaFx/Body/resource/sea-style.css").toExternalForm();
//        stylesheets.add(cssPath);
//    }
}
