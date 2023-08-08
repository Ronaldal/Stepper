package JavaFx.Header;


import JavaFx.AppController;
import Utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.Collection;

public class HeaderController {
    private AppController mainController;
    @FXML
    private VBox headerComponent;
    @FXML
    private Label isManagerLabel;
    @FXML
    private Button greenSytle;
    @FXML
    private Button seaSytle;
    @FXML
    private Button classicSytleButton;

    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRolesLabel;

    private BooleanProperty managerLabel=new SimpleBooleanProperty(false);

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        isManagerLabel.textProperty().bind(Bindings.when(managerLabel).
                then("The user is manager")
                .otherwise("The user is not manager"));
    }

    public void setUsernameLabel(String username){
        userNameLabel.textProperty().setValue(username);
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

    public void setUserRoles(Collection<String> roles){
        String joinedRoles = String.join(",", roles);
        userRolesLabel.textProperty().setValue(joinedRoles);
    }
    public void setIsManagerLabel(Boolean isManager){
        if (managerLabel.get()!=isManager)
            managerLabel.set(isManager);

    }

}
