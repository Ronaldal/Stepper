package JavaFx.Body;

import DTO.FlowDetails.FlowDetails;
import DTO.FlowExecutionData.FlowExecutionData;
import JavaFx.AppController;

import JavaFx.Body.ExecutionData.ExecutionData;
import JavaFx.Body.ExecutionData.FlowExecutionDataImpUI;
import JavaFx.Body.FlowDefinition.FlowDefinition;

import JavaFx.Body.FlowExecution.FlowExecution;
import JavaFx.Body.FlowHistory.FlowHistory;
import JavaFx.Body.FlowStats.FlowStats;

import StepperEngine.Step.api.StepStatus;
import StepperEngine.Stepper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;

import java.util.*;

public class BodyController {

    @FXML private TabPane bodyComponent;
    @FXML private Tab flowDefinitionTab;
    @FXML private FlowDefinition flowDefinitionController;
    @FXML private Tab flowExecutionTab;
    @FXML private FlowExecution flowExecutionController;
    @FXML private Tab flowHistoryTab;
    @FXML private FlowHistory flowHistoryController;
    @FXML private Tab flowStatsTab;

    @FXML private FlowStats flowStatsController;

    private Map<String, ExecutionData> executionDataMap=new HashMap<>();
    private Map<String, Map<String,ExecutionData>> executionStepsInFLow=new HashMap<>();
    private AppController mainController;


    @FXML
    public void initialize(){
        flowDefinitionController.setMainController(this);
        flowExecutionController.setMainController(this);
        flowHistoryController.setMainController(this);
        flowStatsController.setMainController(this);

        bodyComponent.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null && newTab == flowHistoryTab && getStepper() != null) {
                // Update the TableView with information
                flowHistoryController.setFlowsExecutionTable();
            }
        });
    }
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setFlowDetailsList(List<FlowDetails> flowDetails){
        flowDefinitionController.setDataByFlowName(flowDetails);
    }



    public void goToExecuteFlowTab(FlowDetails flow) {
        flowExecutionController.setFlowToExecute(flow);
        bodyComponent.getSelectionModel().select(flowExecutionTab);
    }

    public void updateStats(String flowName){
        flowStatsController.updateStats(flowName);
    }

    public Stepper getStepper(){
        return mainController.getStepper();
    }

    public void updateFlowHistory() {
        flowHistoryController.setFlowsExecutionTable();
    }

    public void initStats(List<String> flowNames){
        flowStatsController.initStats(flowNames);
    }

    public ExecutionData getFlowExecutionData(FlowExecutionData flow){
        if(!executionDataMap.containsKey(flow.getUniqueExecutionId()))
            executionDataMap.put(flow.getUniqueExecutionId(),new FlowExecutionDataImpUI(flow));
        return executionDataMap.get(flow.getUniqueExecutionId());
    }
    public Node getStepExecutionData(FlowExecutionData flow, String stepName){
        return executionDataMap.get(flow.getUniqueExecutionId()).getStepVbox(stepName);
    }
    public ImageView getExecutionStatusImage(String status){
        ImageView imageView ;

        switch (StepStatus.valueOf(status)){
            case WARNING:
                imageView=new ImageView("JavaFx/Body/resource/warning.png");
                break;
            case SUCCESS:
                imageView=new ImageView("JavaFx/Body/resource/success.png");
                break;
            default:
                imageView=new ImageView("JavaFx/Body/resource/fail.png");
                break;
        }
        imageView.setFitHeight(15);
        imageView.setFitWidth(15);
        return imageView;

    }

    public String continuationFlow(String uuidFlow,String flowToContinue){
        return mainController.getStepper().applyContinuation(uuidFlow,flowToContinue);
    }
    public void rerunFlow(FlowExecutionData flow){
        bodyComponent.getSelectionModel().select(flowExecutionTab);
        flowExecutionController.runFlowAgain(getStepper().getFlowsDetailsByName(flow.getFlowName()),mainController.getStepper().reRunFlow(flow.getUniqueExecutionId()));
    }
    public void applyContinuationFromHistoryTab(String pastFlowUUID,String flowToContinue){
        bodyComponent.getSelectionModel().select(flowExecutionTab);
        flowExecutionController.applyContinuation(pastFlowUUID,flowToContinue);
    }

}