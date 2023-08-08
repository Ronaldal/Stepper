package JavaFx.Body.FlowDefinition;

import DTO.FlowDetails.FlowDetails;
import DTO.FlowDetails.StepDetails.FlowIODetails.Input;
import DTO.FlowDetails.StepDetails.FlowIODetails.Output;
import JavaFx.Body.BodyController;
import JavaFx.Body.FlowDefinition.Tabels.FlowDefinitionTable;
import JavaFx.Body.FlowDefinition.Tabels.StepDetailsTable;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FlowDefinition {
    @FXML private Button executeFlow;
    @FXML private Button readOnlyButton;
    @FXML private TableView<FlowDefinitionTable> flowTable;
    @FXML private Label flowName;
    @FXML private Label continuationNumber;
    @FXML private TableColumn<FlowDefinitionTable, String> flowNameCol;
    @FXML private TableColumn<FlowDefinitionTable, String> DescriptionCol;
    @FXML private TableColumn<FlowDefinitionTable,Integer> freeInputs;
    @FXML private TableColumn<FlowDefinitionTable, Integer> stepsNumber;
    @FXML private TableColumn<FlowDefinitionTable, Integer> ContinuationsNumber;
    @FXML private VBox flowsInfo;
    @FXML private Label flowDescription;
    @FXML private Label isReadOnlyFlow;
    @FXML private ListView<String> formalOutputs;
    @FXML private TableView<StepDetailsTable> stepsTable;
    @FXML private TableColumn<StepDetailsTable, String> stepCol;
    @FXML private TableColumn<StepDetailsTable, String> readOnlyCol;
    @FXML private TableView<Input> freeInputsTable;
    @FXML private TableColumn<Input, String> freeInputName;
    @FXML private TableColumn<Input, String> freeInputType;
    @FXML private TableColumn<Input, String> freeInputNecessity;
    @FXML private TableColumn<Input, String> freeInputSteps;
    @FXML private TableView<Output> AllOutputsTable;
    @FXML private TableColumn<Output, String> outputName;
    @FXML private TableColumn<Output, String> outputType;
    @FXML private TableColumn<Output, String> outputSteps;
    @FXML private ChoiceBox<String> stepsSelection;
    @FXML private TableView<Input> stepInputsTable;
    @FXML private TableColumn<Input, String> stepInputsName;
    @FXML private TableColumn<Input, String> stepInputsNecessity;
    @FXML private TableColumn<Input, String> stepInputsConnected;
    @FXML private TableColumn<Input, String> stepInputsFromOutputs;
    @FXML private TableView<Output> stepOutputsTable;
    @FXML private TableColumn<Output, String> stepOutputsNames;
    @FXML private TableColumn<Output, String> stepOutputsConnect;
    @FXML private TableColumn<Output, String> stepOutputsToInput;

    private List<FlowDetails> flowDetails;
    private FlowDetails currSelectedFlow;

    private Map<String ,ChoiceBox<String>> stepsSelectionMap=new HashMap<>();
    private ObservableList<FlowDefinitionTable> flowDefinitionTableObservableList;
    private Map<String,ObservableList<StepDetailsTable>> stepsTableData =new HashMap<>();
    private Map<String,ObservableList<String>> stepsNames =new HashMap<>();
    private Map<String,ObservableList<Input>> freeInputsData=new HashMap<>();
    private Map<String,ObservableList<Output>> outputsData=new HashMap<>();
    private BodyController bodyController;





    @FXML
    void executeFlow(ActionEvent event) {
        bodyController.goToExecuteFlowTab(currSelectedFlow);
    }

    @FXML
    void tableMouseClick(MouseEvent event) {
        if(event.getClickCount()==2) {
            FlowDetails currFlow = flowDetails.get(flowTable.getSelectionModel().getSelectedIndex());
            currSelectedFlow = currFlow;
            setDataByFlowName(currFlow);
            createFormalOutputsList(currFlow);
            createStepTable(currFlow);
            creatFreeInputsTable(currFlow);
            creatOutputsTable(currFlow);
            stepsSelection.setItems(stepsNames.get(currFlow.getFlowName()));

        }
    }

    /**
     * if some step selected from choice box some we need to update two table.
     * this function handel it.
     * @param event
     */
    public void setSelectedStepTables(ActionEvent event){
        FlowDetails currFlow = currSelectedFlow;
        int stepIndex=stepsNames.get(currFlow.getFlowName()).indexOf(stepsSelection.getValue());
        if(stepIndex>=0) {
            ObservableList<Input> currStepInputs = stepsTableData.get(currFlow.getFlowName()).get(stepIndex).getInputs();
            ObservableList<Output> currStepOutputs = stepsTableData.get(currFlow.getFlowName()).get(stepIndex).getOutputs();
            creteStepInputTable(currStepInputs);
            creteStepOutputTable(currStepOutputs);
        }
        else{//so new flow selected that clear the step table of the previous flow
            stepOutputsTable.getItems().clear();
            stepInputsTable.getItems().clear();
        }
    }

    /***
     * sets the header of the flow definition screen .
     * @param currFlow the current flow that selected from flowDefinition table.
     */
    private void setDataByFlowName(FlowDetails currFlow) {
        flowName.setText(currFlow.getFlowName());
        flowDescription.setText(currFlow.getFlowDescription());
        if (currFlow.isFlowReadOnly()) {
            readOnlyButton.setText("Yes");
            readOnlyButton.getStyleClass().setAll("status-button", "v-button");;
        } else {
            readOnlyButton.setText("No");
            readOnlyButton.getStyleClass().setAll("status-button", "x-button");;
        }
        continuationNumber.setText("The number of continuation's : "+currFlow.getContinuationNumber());
    }


    private void createFormalOutputsList(FlowDetails currFlow) {
        ObservableList<String> observableListFormalOutputs = FXCollections.observableList(currFlow.getFormalOutputs());
        formalOutputs.setItems(observableListFormalOutputs);//List view of formal outputs
    }


    /*
    The method updates data on each flow according to its name to map.
    For each flow, a mapping of the list of steps, a mapping of the free inputs
     */
    public void setDataByFlowName(List<FlowDetails> flowDetails) {
        this.flowDetails = flowDetails;
        List<FlowDefinitionTable> flowDefinitionTableList= new ArrayList<>();
        for (FlowDetails flow :flowDetails){
            flowDefinitionTableList.add(new FlowDefinitionTable(flow));
            freeInputsData.put(flow.getFlowName(),FXCollections.observableList(flow.getFreeInputs()));
            ChoiceBox<String> stepSelection=new ChoiceBox<>();
            stepSelection.setItems(FXCollections.observableList(flow.getStepsNames()));
            stepsSelectionMap.put(flow.getFlowName(),stepSelection);
            outputsData.put(flow.getFlowName(),FXCollections.observableList(flow.getOutputs()));
            stepsNames.put(flow.getFlowName(),FXCollections.observableList(flow.getStepsNames()));
            stepsTableData.put(flow.getFlowName(),
                    FXCollections.observableList(
                            flow.getSteps().stream().map(StepDetailsTable::new).collect(Collectors.toList())));
        }
        this.flowDefinitionTableObservableList= FXCollections.observableList(flowDefinitionTableList);
        updateTableView();

    }

    public void setMainController(BodyController bodyController) {
        this.bodyController = bodyController;
        stepsSelection.setOnAction(this::setSelectedStepTables);
    }

    /***
     * All function from here are creating the tables
     */

    private void creteStepOutputTable(ObservableList<Output> currStepOutputs) {
        stepOutputsNames.setCellValueFactory(new PropertyValueFactory<Output,String>("dataName"));
        stepOutputsNames.setPrefWidth((currStepOutputs.stream()
                .mapToDouble(input -> input.getDataName().length())
                .max().orElse(-1))*9.5);
        stepOutputsConnect.setCellValueFactory(new PropertyValueFactory<Output,String>("connectedToStep"));
        stepOutputsConnect.setPrefWidth((currStepOutputs.stream()
                .mapToDouble(input -> input.getConnectedToStep().length())
                .max().orElse(-1))*9.5);
        stepOutputsToInput.setCellValueFactory(new PropertyValueFactory<Output,String>("toInput"));
        stepOutputsToInput.setPrefWidth((currStepOutputs.stream()
                .mapToDouble(input -> input.getToInput().length())
                .max().orElse(-1))*9.5);
        stepOutputsTable.setItems(currStepOutputs);
    }

    private void creteStepInputTable(ObservableList<Input> currStepInputs) {
        stepInputsName.setCellValueFactory(new PropertyValueFactory<Input,String>("dataName"));
        stepInputsName.setPrefWidth((currStepInputs.stream()
                .mapToDouble(input -> input.getDataName().length())
                .max().orElse(-1))*9.5);
        stepInputsNecessity.setCellValueFactory(new PropertyValueFactory<Input,String>("necessity") );
        stepInputsConnected.setCellValueFactory(new PropertyValueFactory<Input,String>("connectedToStep"));
        stepInputsConnected.setPrefWidth((currStepInputs.stream()
                .mapToDouble(input -> input.getConnectedToStep().length())
                .max().orElse(-1))*7.5);
        stepInputsFromOutputs.setCellValueFactory(new PropertyValueFactory<Input,String>("formOutput"));
        stepInputsFromOutputs.setPrefWidth((currStepInputs.stream()
                .mapToDouble(input -> input.getFormOutput().length())
                .max().orElse(-1))*7.5);
        stepInputsTable.setItems(currStepInputs);
    }

    private void creatFreeInputsTable(FlowDetails currFlow) {
        freeInputName.setCellValueFactory(new PropertyValueFactory<Input,String>("dataName"));
        freeInputName.setPrefWidth((freeInputsData.get(currFlow.getFlowName()).stream()
                .mapToDouble(input -> input.getDataName().length())
                .max().orElse(-1))*9.8);
        freeInputType.setCellValueFactory(new PropertyValueFactory<Input,String>("typeName"));
        freeInputNecessity.setCellValueFactory(new PropertyValueFactory<Input,String>("necessity"));
        freeInputSteps.setCellValueFactory(new PropertyValueFactory<Input,String>("relatedStepsString"));
        freeInputSteps.setPrefWidth((freeInputsData.get(currFlow.getFlowName()).stream()
                .mapToDouble(input -> input.getRelatedStepsString().length())
                .max().orElse(-1))*9.5);
        freeInputsTable.setItems(freeInputsData.get(currFlow.getFlowName()));
    }

    private void creatOutputsTable(FlowDetails currFlow) {
        outputName.setCellValueFactory(new PropertyValueFactory<Output,String>("dataName"));
        outputName.setPrefWidth((outputsData.get(currFlow.getFlowName()).stream()
                .mapToDouble(output -> output.getDataName().length())
                .max().orElse(-1))*9.5);
        outputType.setCellValueFactory(new PropertyValueFactory<Output,String>("typeName"));
        outputSteps.setPrefWidth((outputsData.get(currFlow.getFlowName()).stream()
                .mapToDouble(output -> output.getTypeName().length())
                .max().orElse(-1))*9.5);
        outputSteps.setCellValueFactory(new PropertyValueFactory<Output,String>("stepRelated"));
        outputSteps.setPrefWidth((outputsData.get(currFlow.getFlowName()).stream()
                .mapToDouble(output -> output.getStepRelated().length())
                .max().orElse(-1))*7.5);
        AllOutputsTable.setItems(outputsData.get(currFlow.getFlowName()));
    }

    private void createStepTable(FlowDetails currFlow) {
        stepCol.setCellValueFactory(new PropertyValueFactory<StepDetailsTable,String>("stepName"));
        stepCol.setPrefWidth(stepsTableData.get(currFlow.getFlowName()).stream()
                .mapToDouble(step -> step.getStepName().length()).max().orElse(-1)*7.5);
        readOnlyCol.setCellValueFactory(new PropertyValueFactory<StepDetailsTable,String>("isReadOnly"));
        stepsTable.setItems(stepsTableData.get(currFlow.getFlowName()));
    }
    /*
    Updates the flows table (the main table in the left)
     */
    public void updateTableView(){
        flowNameCol.setCellValueFactory(new PropertyValueFactory<FlowDefinitionTable,String>("flowName"));
        setColumnWidthToMaxNameLength(flowNameCol);
        DescriptionCol.setCellValueFactory(new PropertyValueFactory<FlowDefinitionTable,String>("description"));
        setColumnWidthToMaxDescriptionLength(DescriptionCol);
        stepsNumber.setCellValueFactory(new PropertyValueFactory<FlowDefinitionTable,Integer>("stepsNumber"));
        freeInputs.setCellValueFactory(new PropertyValueFactory<FlowDefinitionTable,Integer>("freeInputsNumber"));
        ContinuationsNumber.setCellValueFactory(new PropertyValueFactory<FlowDefinitionTable,Integer>("continuationsNumber"));
        flowTable.setItems(flowDefinitionTableObservableList);
    }
    /*
    sets thw width of the description column in flow main table
     */
    private void setColumnWidthToMaxDescriptionLength(TableColumn<FlowDefinitionTable, String> column) {
        int maxWidth=-1;
        column.setPrefWidth(maxWidth);
        for(FlowDetails flow:flowDetails){
            if(flow.getFlowDescription().length()>maxWidth)
                maxWidth=flow.getFlowDescription().length();

        }
        column.setPrefWidth(maxWidth*7.0);
    }
    /*
    sets thw width of the name column in flow main table
    */
    private void setColumnWidthToMaxNameLength(TableColumn<FlowDefinitionTable, String> column) {
        double maxWidth=-1;
        for(FlowDetails flow:flowDetails){
            if(flow.getFlowName().length()>maxWidth)
                maxWidth=flow.getFlowName().length();
        }
        column.setPrefWidth(maxWidth*7.5);
    }

}
