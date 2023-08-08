package JavaFx.Body.FlowExecution;


import DTO.FlowDetails.FlowDetails;
import DTO.FlowDetails.StepDetails.FlowIODetails.Input;
import DTO.FlowExecutionData.FlowExecutionData;
import JavaFx.Body.BodyController;

import StepperEngine.DataDefinitions.Enumeration.ZipEnumerator;
import StepperEngine.Flow.execute.ExecutionNotReadyException;
import StepperEngine.Flow.execute.StepData.StepExecuteData;
import StepperEngine.Step.api.DataNecessity;
import StepperEngine.Stepper;

import javafx.animation.FadeTransition;
import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javafx.util.Duration;

import java.io.File;
import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

public class FlowExecution {
    private static final int INPUT_NAME_COLUMN = 0;
    private static final int INPUT_MANDATORY_COLUMN = 1;
    private static final int INPUT_DATA_DISPLAY_COLUMN = 2;

    @FXML private AnchorPane flowExecutionAnchorPane;
    @FXML private GridPane executionDetailsGridPane;
    @FXML private ListView<String> stepInputListView;
    @FXML private ListView<String> stepOutputListView;
    @FXML private ListView<String> formalOutputsListView;
    @FXML private ListView<String> stepsInfoListView;
    @FXML private Label executionUuidLabel;
    @FXML private Label executionTimestampLabel;
    @FXML private Label executionResultLabel;
    @FXML private Separator executionDetailsDataSeperator;
    @FXML private Separator StepOutputSeperator;
    @FXML private Label stepNameDisplayNameLabel;
    @FXML private GridPane freeInputsGridPane;
    @FXML private ScrollPane freeInputsScrollPane;
    @FXML private TableView<FreeInputsTableRow> freeInputsTableView;
    @FXML private TableColumn<FreeInputsTableRow, String> freeInputNameCol;
    @FXML private TableColumn<FreeInputsTableRow, String> freeInputValueCol;
    @FXML private TableColumn<FreeInputsTableRow, String> freeInputApiNameCol;
    @FXML private Label outputNameDisplayNameLabel;
    @FXML private ImageView flowExecutionButtonImage;
    @FXML private ProgressBar executionProgressBar;
    @FXML private Label floeDetailsLabel;
    @FXML private GridPane flowDetailGridPane;
    @FXML private Label flowNameLabel;
    @FXML private Label floeDescriptionLabel;
    @FXML private Label floeStepsLabel;
    @FXML private ImageView continuationButtonImage;
    @FXML private ChoiceBox<String> continuationChoiceBox;
    @FXML private Label stepNameTitleLabel;
    @FXML private GridPane stepDetailsGridPane;
    @FXML private AnchorPane outputPresentationAnchorPane;
    @FXML private Label stepLogsLabel;
    @FXML private Label stepResutLabel;
    @FXML private AnchorPane stepIODataDisplayAnchorPane;
    @FXML private Label stepTimeLabel;
    @FXML private Label CentralFlowName;
    @FXML private TreeView<String> StepsTreeVIew;
    @FXML private VBox MainExecutionDataVbox;
    @FXML private Button rerunButton;



    private BodyController bodyController;
    private FlowDetails flowDetails;
    private FlowExecutionData flowExecutionData;
    private FlowExecutionData flowExecutionDataImp;

    private String lastFlowRunningUuid;
    private String currFlowExecutionUuid;

    @FXML
    void initialize(){
        initButtons();
        freeInputNameCol.setCellValueFactory(new PropertyValueFactory<>("apiName"));
        freeInputValueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        addFreeInputsFirstRow();
        continuationChoiceBox.setOnAction(this::startContinuation);
    }
    public void startContinuation(ActionEvent event){
        makeContinuationButtonEnabled();
    }
    @FXML
    void continueFlow(MouseEvent event) {
        String flowToContinue=continuationChoiceBox.getValue();
        applyContinuation(flowExecutionData.getUniqueExecutionId(),flowToContinue);
    }
    @FXML
    void rerunFlow(ActionEvent event) {
        bodyController.rerunFlow(flowExecutionDataImp);
    }


    public void applyContinuation(String UUID,String flowToContinue) {
        currFlowExecutionUuid=bodyController.continuationFlow(UUID, flowToContinue);
        flowDetails=bodyController.getStepper().getFlowsDetailsByName(flowToContinue);
        updateFlowExecutionData();
    }

    public void runFlowAgain(FlowDetails flow, String UUID){
        flowDetails=flow;
        currFlowExecutionUuid=UUID;
        updateFlowExecutionData();
    }

    private void updateFlowExecutionData() {
        cleanUpScreen();
        setFreeInputsDisplay();
        Map<String, Object> allData = bodyController.getStepper().getFlowExecutionByUuid(currFlowExecutionUuid).getFreeInputsValue();
        for(Input input:flowDetails.getFreeInputs()){
            if(allData.containsKey(input.getFullQualifideName())) {
                addNewValue(input, allData.get(input.getFullQualifideName()).toString());
                addInputToTable(input, allData.get(input.getFullQualifideName()).toString());
            }
        }
        CentralFlowName.setText(flowDetails.getFlowName());
        continuationChoiceBox.getItems().clear();
        initContinuationButton();
        setContinuation();
        initRerunButton();
    }

    @FXML
    void executeFlow(MouseEvent event) {
        if(flowExecutionButtonImage.opacityProperty().get() == 1) {
            try {
                executionProgressBar.setProgress(0);
                bodyController.getStepper().executeFlow(currFlowExecutionUuid);
                initExecuteButton();
                new Thread(new Runnable() {
                    String uuid = currFlowExecutionUuid;
                    @Override
                    public void run() {
                        executeFlowTask(uuid);
                    }
                }).start();
                lastFlowRunningUuid = currFlowExecutionUuid;
            } catch (ExecutionNotReadyException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }


    void executeFlowTask(String uuid) {
        synchronized (this) {
            Stepper stepper = bodyController.getStepper();
            while (!stepper.isExecutionEnded(uuid)) {
                Platform.runLater(() -> executionProgressBar.setProgress(stepper.getExecutionPartialStatus(uuid)));
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Platform.runLater(() ->{
            executionProgressBar.setProgress(1);
            flowExecutionDataImp=new FlowExecutionData(bodyController.getStepper().getFlowExecutionByUuid(uuid));
            flowExecutionData =flowExecutionDataImp ;
            bodyController.updateStats(flowExecutionData.getFlowName());
            cleanUpExecutionDetails();
            setExecutionDetails();
            makeRerunButtonEnabled();
        });
    }

    void setExecutionDetails() {
        executionProgressBar.setProgress(1);
        CentralFlowName.setText( flowExecutionDataImp.getFlowName());
        MainExecutionDataVbox.getChildren().add(bodyController.getFlowExecutionData(flowExecutionDataImp).getVbox());
        TreeItem root = new TreeItem(flowExecutionDataImp.getFlowName(), bodyController.getExecutionStatusImage(flowExecutionDataImp.getExecutionResult()));
        StepsTreeVIew.setRoot(root);
        for (DTO.FlowExecutionData.StepExecuteData step : flowExecutionDataImp.getStepExecuteDataList()) {
            TreeItem<String> childItem = new TreeItem<>(step.getFinalName(), bodyController.getExecutionStatusImage(step.getStepStatus().toString()));
            root.getChildren().add(childItem);
        }
        bodyController.updateFlowHistory();
        setContinuation();
    }
    @FXML
    void setStepData(MouseEvent event) {
        TreeItem<String> selectedItem = StepsTreeVIew.getSelectionModel().getSelectedItem();
        if(selectedItem!=null){
            MainExecutionDataVbox.getChildren().clear();
            boolean isRoot = selectedItem.getParent() == null;
            if (isRoot)
                MainExecutionDataVbox.getChildren().add(bodyController.getFlowExecutionData(flowExecutionDataImp).getVbox());
            else {
                MainExecutionDataVbox.getChildren().add(bodyController.getStepExecutionData(flowExecutionDataImp, selectedItem.getValue()));
            }
        }
    }



    private void addFreeInputsFirstRow() {
        freeInputsGridPane.add(new Label("Name"), INPUT_NAME_COLUMN, 0);
        freeInputsGridPane.add(new Label("Is madantory?"), INPUT_MANDATORY_COLUMN, 0);
        freeInputsGridPane.add(new Label(), INPUT_DATA_DISPLAY_COLUMN, 0);
    }

    private void initButtons() {
        initExecuteButton();
        initContinuationButton();
        initRerunButton();
    }
    private void initRerunButton()
    {
        rerunButton.opacityProperty().set(0.2);
        rerunButton.cursorProperty().set(Cursor.DISAPPEAR);
    }
    private void makeRerunButtonEnabled(){
        rerunButton.opacityProperty().set(1);
        rerunButton.cursorProperty().set(Cursor.HAND);
    }

    private void initContinuationButton() {
        continuationButtonImage.opacityProperty().set(0.2);
        continuationButtonImage.cursorProperty().set(Cursor.DISAPPEAR);
    }
    private void makeContinuationButtonEnabled() {
        continuationButtonImage.opacityProperty().set(1);
        continuationButtonImage.cursorProperty().set(Cursor.HAND);
    }

    private void initExecuteButton() {
        flowExecutionButtonImage.opacityProperty().set(0.2);
        flowExecutionButtonImage.cursorProperty().set(Cursor.DISAPPEAR);
    }

    void makeExecutionButtonEnabled(){
        flowExecutionButtonImage.opacityProperty().set(1);
        flowExecutionButtonImage.cursorProperty().set(Cursor.HAND);
    }

    public void setMainController(BodyController bodyController) {
        this.bodyController = bodyController;
    }

    public void setFlowToExecute(FlowDetails flow){
        cleanUpScreen();
        flowDetails = flow;
        currFlowExecutionUuid = bodyController.getStepper().createNewExecution(flow.getFlowName());
        //setFlowDetails();
        CentralFlowName.setText(flow.getFlowName());
        setFreeInputsDisplay();
    }


    public void setFreeInputsDisplay(){
        for(int i = 0; i < flowDetails.getFreeInputs().size(); i++){
            setInputRowData(flowDetails.getFreeInputs().get(i), i + 1);
            freeInputsScrollPane.setVvalue(freeInputsScrollPane.getVmax());
            freeInputsTableView.getItems().add(new FreeInputsTableRow(flowDetails.getFreeInputs().get(i), "not provided"));
        }
    }

    public void setInputRowData(Input input, int row){
        freeInputsGridPane.add(new Label(input.getDataName()), INPUT_NAME_COLUMN, row);
        freeInputsGridPane.add(new Label(
                DataNecessity.valueOf(input.getNecessity()).equals(DataNecessity.MANDATORY)? "Yes":"NO"),
                INPUT_MANDATORY_COLUMN,
                row);
        freeInputsGridPane.add(getInputDataDisplayer(input), INPUT_DATA_DISPLAY_COLUMN, row);
        freeInputsGridPane.getRowConstraints().get(freeInputsGridPane.getRowConstraints().size() - 1).setPrefHeight(Region.USE_COMPUTED_SIZE);
    }

    public Node getInputDataDisplayer(Input input){
        String typeName = input.getTypeName();
        if(typeName.equals("Enumerator")){
            return getEnumeratorChoiceBox(input);
        }
        else if(typeName.equals("File path") || typeName.equals("Folder path")){
            return getFileChooserButton(input);
        }
        else{
            return getTextFieldChooser(input);
        }
    }

    private ChoiceBox<String> getEnumeratorChoiceBox(Input input) {
        ChoiceBox<String> choiceBox = new ChoiceBox<>(FXCollections.observableArrayList(EnumSet.allOf(ZipEnumerator.class).stream()
                .map(Enum::toString)
                .collect(Collectors.toList())));
        choiceBox.setOnAction(event -> {
            if(choiceBox.getValue() != null){
                boolean isAdded = addNewValue(input, choiceBox.getValue());
                if(!isAdded){
                    choiceBox.setValue(null);
                }
                else {
                    addInputToTable(input, choiceBox.getValue());
                }
            }
        });
        return choiceBox;
    }



    public HBox getFileChooserButton(Input input){
        HBox hBox = new HBox();
        Tooltip existinFile = new Tooltip("choose existing file");
        Tooltip existingFolder = new Tooltip("choose existing folder");
        Tooltip newFile = new Tooltip("choose new file");
        ImageView fileChooserButton = new ImageView();
        ImageView folderChooserButton = new ImageView();
        TextField textField = new TextField();
        Tooltip.install(fileChooserButton, existinFile);
        Tooltip.install(folderChooserButton, existingFolder);
        Tooltip.install(textField, newFile);
        if(input.getTypeName().equals("Folder path")){
            hBox.getChildren().add(folderChooserButton);
        }
        else {
            hBox.getChildren().add(textField);
            hBox.getChildren().add(fileChooserButton);
            hBox.getChildren().add(folderChooserButton);
        }
        EventHandler<MouseEvent> directoryHandler = event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File folderChoose = directoryChooser.showDialog(folderChooserButton.getScene().getWindow());
            if(folderChoose != null)
                if(addNewValue(input, folderChoose.getAbsolutePath())){
                    addInputToTable(input, folderChoose.getName());
                }
        };
        EventHandler<MouseEvent> fileHandler = event -> {
            FileChooser fileChooser = new FileChooser();
            File fileChoose = fileChooser.showOpenDialog(fileChooserButton.getScene().getWindow());
            if(fileChoose!=null){
                if(addNewValue(input, fileChoose.getAbsolutePath())){
                    addInputToTable(input, fileChoose.getName());
                }
            }
        };
        EventHandler<ActionEvent> newFileNameHandler = event -> {
            String newFileName = textField.getText();
            if(!newFileName.isEmpty()){
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File folderChoose = directoryChooser.showDialog(fileChooserButton.getScene().getWindow());
                if(folderChoose != null){
                    String folderChoosePath = folderChoose.getAbsolutePath();
                    String newFilePath = String.format("%s\\%s", folderChoosePath, newFileName);
                    if(addNewValue(input, newFilePath)){
                        addInputToTable(input, newFileName);
                    }
                }
            }
            textField.clear();
        };
        fileChooserButton.setImage(new Image(getClass().getResourceAsStream("fileIcon.png")));
        fileChooserButton.setFitHeight(30);
        fileChooserButton.setFitWidth(30);
        fileChooserButton.setOnMouseClicked(fileHandler);
        folderChooserButton.setImage(new Image(getClass().getResourceAsStream("folderIcon.png")));
        folderChooserButton.setFitWidth(30);
        folderChooserButton.setFitHeight(30);
        folderChooserButton.setOnMouseClicked(directoryHandler);
        textField.setOnAction(newFileNameHandler);
        return hBox;
    }
    public HBox getTextFieldChooser(Input input){
        HBox hBox = new HBox();
        TextField textField = new TextField();
        Button addButton = new Button("+");
        Label invalidInputLabel = new Label();
        invalidInputLabel.setTextFill(Color.RED);
        invalidInputLabel.setPadding(new Insets(0, 0, 0, 10));
        hBox.getChildren().add(textField);
        hBox.getChildren().add(addButton);
        hBox.getChildren().add(invalidInputLabel);
        EventHandler<ActionEvent> eventHandler = event -> {
            if(!addNewValue(input, textField.getText())){
                invalidInputLabel.textProperty().setValue("invalid input");
                FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), invalidInputLabel);
                fadeTransition.setFromValue(1.0);
                fadeTransition.setToValue(0.0);
                fadeTransition.setCycleCount(5);
                fadeTransition.play();
            }
            else{
                invalidInputLabel.textProperty().setValue("");
                addInputToTable(input, textField.getText());
            }
        };
        addButton.setOnAction(eventHandler);
        textField.setOnAction(eventHandler);
        return hBox;
    }





    private boolean addNewValue(Input input, String value) {
        try {
            boolean result = bodyController.getStepper().addFreeInputToExecution(currFlowExecutionUuid, input.getDataName(),
                    convertValue(value, input.getTypeName()));
            if(result && bodyController.getStepper().getExecutionReadyToBeExecuteStatus(currFlowExecutionUuid)){
                makeExecutionButtonEnabled();
            }
            return result;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public Object convertValue(String value, String type){
        if (type.equals("Number")) {
            return Integer.parseInt(value);
        } else if (type.equals("Double")) {
            return Double.parseDouble(value);
        } else {
            return value;
        }
    }

    public void addInputToTable(Input input, String newVal){
        ObservableList<FreeInputsTableRow> freeInputsTableRows = freeInputsTableView.getItems();
        for(int i=0; i<freeInputsTableRows.size(); i++){
            if(freeInputsTableRows.get(i).getApiName().equals(input.getDataName())){
                freeInputsTableRows.get(i).setValue(newVal);
                freeInputsTableView.getItems().set(i, freeInputsTableRows.get(i));
                return;
            }
        }
    }

    public void cleanUpScreen(){
        cleanUpFreeInputs();
        cleanUpExecutionDetails();
    }

    private void cleanUpExecutionDetails() {
        MainExecutionDataVbox.getChildren().clear();
        executionProgressBar.setProgress(0.0);
        if(StepsTreeVIew.getRoot()!= null) {
            StepsTreeVIew.getRoot().getChildren().clear();
            StepsTreeVIew.setRoot(null);
        }
    }

    private void cleanUpFreeInputs() {
        freeInputsTableView.getItems().remove(0, freeInputsTableView.getItems().size());
        freeInputsGridPane.getChildren().clear();
        addFreeInputsFirstRow();
    }

    public void setStepInputListView(StepExecuteData step){
        stepInputListView.setItems(FXCollections.observableArrayList(step.getDataMap().keySet()));
    }

    public void setContinuation(){
        continuationChoiceBox.setItems(FXCollections.observableArrayList(flowDetails.getContinuationNames()));
    }


}
