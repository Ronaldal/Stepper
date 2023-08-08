package JavaFx.Header;
import JavaFx.AppController;
import StepperEngine.Flow.FlowBuildExceptions.FlowBuildException;
import StepperEngine.StepperReader.Exception.ReaderException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HeaderController {
    private AppController mainController;
    @FXML
    private VBox headerComponent;

    @FXML
    private Button loadButton;
    @FXML
    private Button greenSytle;

    @FXML
    private Button seaSytle;

    @FXML
    private Button classicSytleButton;
    @FXML
    private Label filePathLabel;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
    @FXML
    private void handleLoadButtonClick() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        Stage stage = (Stage) loadButton.getScene().getWindow();
        // Show the file chooser dialog
        java.io.File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String filePath = selectedFile.getPath();
            if(mainController.loadFile(filePath))
                filePathLabel.setText(filePath);
        }

    }
    @FXML
    void classicSytleButton(ActionEvent event) {
        BorderPane root = (BorderPane) classicSytleButton.getScene().getRoot();
        ObservableList<String> stylesheets = root.getStylesheets();;
        stylesheets.clear();

        // Add the path to the new stylesheet
        String cssPath = getClass().getResource("/JavaFx/Body/resource/classic-style.css").toExternalForm();
        stylesheets.add(cssPath);
    }

    @FXML
    void greenSytleButton(ActionEvent event) {
        BorderPane root = (BorderPane) greenSytle.getScene().getRoot();
        ObservableList<String> stylesheets = root.getStylesheets();;
        stylesheets.clear();

        // Add the path to the new stylesheet
        String cssPath = getClass().getResource("/JavaFx/Body/resource/water-melon-style.css").toExternalForm();
        stylesheets.add(cssPath);
    }

    @FXML
    void seaSytleButton(ActionEvent event) {
        BorderPane root = (BorderPane) seaSytle.getScene().getRoot();
        ObservableList<String> stylesheets = root.getStylesheets();;
        stylesheets.clear();

        // Add the path to the new stylesheet
        String cssPath = getClass().getResource("/JavaFx/Body/resource/sea-style.css").toExternalForm();
        stylesheets.add(cssPath);
    }
}
