package JavaFx.Body.ExecutionData;

import DTO.FlowExecutionData.FlowExecutionData;
import DTO.FlowExecutionData.IOData;
import DTO.FlowExecutionData.StepExecuteData;
import DataPresenter.DataPresentation;
import DataPresenter.DataPresentationImpl;
import JavaFx.Body.ExecutionData.Step.StepExecutionDataImpUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FlowExecutionDataImpUI implements ExecutionData{
    private FlowExecutionData flow;
    private VBox flowVbox =new VBox();
    private HBox name;
    private HBox userName;
    private HBox UUID;
    private HBox flowExecutionStatus;
    private HBox timeStamp;
    private HBox freeInputs;


    private Map<String,ExecutionData> stepsData=new HashMap<>();
    private DataPresentation dataPresentation=new DataPresentationImpl();
    private Map<String, StepExecutionDataImpUI> stepsMap=new HashMap<>();
    public FlowExecutionDataImpUI(FlowExecutionData flow) {
        this.flow = flow;
        this.name=setTwoLabels("Flow Name :", flow.getFlowName());
        this.userName=setTwoLabels("User Identity:",flow.getUserExecuted()+" is "+flow.getManager());
        this.UUID=setTwoLabels("UUID :", flow.getUniqueExecutionId());
        this.flowExecutionStatus=setTwoLabels("Flow execution status :", flow.getExecutionResult());
        this.timeStamp=setTwoLabels("Timestamp", flow.getExecutionTime());
        freeInputs=getFreeInputs(flow);
        flowVbox.setSpacing(5);
        flowVbox.getChildren().addAll(
                name,userName,UUID,flowExecutionStatus,timeStamp,freeInputs,new Separator(),
                new Label("All Outputs :"),dataPresentation.getDataPresent(flow.getOutputs()));
        createStepsMap();

    }


    private void createStepsMap() {
        flow.getStepExecuteDataList().forEach(
                this::createStepExecutionData);
    }

    private void createStepExecutionData(StepExecuteData step) {
        stepsMap.put(step.getFinalName(),new StepExecutionDataImpUI(step));
    }

    @Override
    public Node getStepVbox(String stepName) {
        return stepsMap.get(stepName).getVbox();
    }

    public HBox setTwoLabels(String name, String value) {
        HBox hBox=new HBox();
        Label nameLabel=new Label(name);
        Label valueLabel=new Label(value);
        hBox.getChildren().add(nameLabel);
        hBox.getChildren().add(valueLabel);
        hBox.setSpacing(5);
        hBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        hBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        return hBox;
    }

    public TableView<IOData> getFreeInputTable(FlowExecutionData flowExecutionData) {
        TableView<IOData> tableView=new TableView<>();
        TableColumn<IOData, String> freeInputName=new TableColumn<>("Name");
        TableColumn<IOData, String> freeInputType=new TableColumn<>("Type");
        TableColumn<IOData, String> freeInputContent=new TableColumn<>("Content");
        TableColumn<IOData, String> freeInputNecessity=new TableColumn<>("Necessity");

        double totalWidth = 0.15 + 0.2 + 0.2 + 0.2; // Calculate the total sum of the column ratios
        freeInputName.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15 / totalWidth));
        freeInputType.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2 / totalWidth));
        freeInputNecessity.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2 / totalWidth));
        freeInputName.setCellValueFactory(new PropertyValueFactory<IOData,String>("name"));
        freeInputType.setCellValueFactory(new PropertyValueFactory<IOData,String>("type"));
        freeInputContent.setCellValueFactory(new PropertyValueFactory<IOData,String>("content"));
        freeInputNecessity.setCellValueFactory(new PropertyValueFactory<IOData,String>("necessity"));
        tableView.getColumns().addAll(freeInputName,freeInputType,freeInputContent,freeInputNecessity);
        tableView.setItems(FXCollections.observableList(new ArrayList<>(flowExecutionData.getFreeInputs())));
        tableView.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                tableView.setMaxWidth(newValue.doubleValue());
            }
        });
        tableView.setMinHeight(150);
        tableView.setMaxHeight(200);
        List<IOData> items = tableView.getItems();
        freeInputContent.setPrefWidth(getMaxContentLen(freeInputContent, items)*9.5);
        freeInputContent.setMaxWidth(200);
        freeInputContent.setMinWidth(100);
        return tableView;
    }

    private int getMaxContentLen(TableColumn<IOData, String> freeInputContent, List<IOData> items) {
        int maxLength = 0;
        for (IOData item : items) {
            String content = freeInputContent.getCellData(item);
            if (content != null) {
                maxLength = Math.max(maxLength, content.length());
            }
        }
        return maxLength;
    }

    public HBox getFreeInputs(FlowExecutionData flowExecutionData) {
        HBox hBox=new HBox();
        Label freeInputsLabel=new Label("Free inputs:");
        hBox.getChildren().add(freeInputsLabel);
        TableView<IOData> freeInputTable = getFreeInputTable(flowExecutionData);
        hBox.getChildren().add(freeInputTable);
        hBox.setSpacing(5);
        HBox.setHgrow(freeInputTable, Priority.ALWAYS);
        hBox.setMaxWidth(1000);
        return hBox;
    }

    @Override
    public VBox getVbox() {
        return flowVbox;
    }
}
