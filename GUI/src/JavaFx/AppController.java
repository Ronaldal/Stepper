package JavaFx;

import DTO.StepperDTO;
import JavaFx.Body.BodyController;
import JavaFx.Header.HeaderController;
import StepperEngine.Flow.FlowBuildExceptions.FlowBuildException;
import StepperEngine.Flow.execute.FlowExecution;
import StepperEngine.Stepper;
import StepperEngine.StepperReader.Exception.ReaderException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.awt.event.ActionEvent;
import java.util.Optional;

public class AppController {
    @FXML private VBox headerComponent;
    @FXML private TabPane bodyComponent;
    @FXML HeaderController headerComponentController;
    @FXML BodyController bodyComponentController;
    private final StepperDTO stepperDTO=new StepperDTO();

    private Stepper stepper;
    boolean isStepperIn=false;

    @FXML
    public void initialize() {

        headerComponentController.setMainController(this);
        bodyComponentController.setMainController(this);
   //     loadFile("C:\\Users\\Gil\\Desktop\\StepperClone\\ex2 (1).xml");
    }
    public boolean loadFile(String filePath) {
        try {

            stepperDTO.load(filePath);
            bodyComponentController.setFlowDetailsList(stepperDTO.getFlowsDetailsList());
            stepper = stepperDTO.getStepper();
            bodyComponentController.initStats(stepper.getFlowNames());
            return true;
        }catch (ReaderException | FlowBuildException | RuntimeException e ) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Stepper");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return false;
        }
    }


    public Stepper getStepper() {
        return stepper;
    }
    public void changeCSS(){

    }
}
