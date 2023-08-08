package JavaFx.Body.ExecutionData.Step;

import DTO.FlowExecutionData.StepExecuteData;
import DataPresenter.DataPresentation;
import DataPresenter.DataPresentationImpl;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class StepExecutionDataImpUI {

    private StepExecuteData stepExecuteData;
    private VBox StepVbox =new VBox();
    private HBox name;
    private HBox startTime;
    private HBox endTime;
    private HBox timeStamp;
    private HBox stepExecutionStatus;
    private HBox logs;
    private DataPresentation dataPresentation=new DataPresentationImpl();

    public StepExecutionDataImpUI(DTO.FlowExecutionData.StepExecuteData step) {
        this.stepExecuteData = step;
        this.name=setTwoLabels("Step Name :",step.getFinalName());
        this.startTime=setTwoLabels("Start time : ",step.getFormattedStartTime());
        this.endTime=setTwoLabels("End time : ",step.getFormattedEndTime());
        this.timeStamp=setTwoLabels("Duration : ",String.format("%02d",step.getTotalTime().toMillis())+" mils");
        this.stepExecutionStatus=setTwoLabels("Execution Status : ",step.getStepStatus().toString());
        this.logs=getLogs(step.getLogs());
        StepVbox.setSpacing(5);
        StepVbox.getChildren().addAll(
                name,startTime,endTime,timeStamp,stepExecutionStatus,new Separator(),
                new Label("All Inputs :"),dataPresentation.getDataPresent(step.getInputs()),
                new Separator(),
                new Label("All Outputs :"),dataPresentation.getDataPresent(step.getOutputs()),
                new Separator(),logs);
    }

    private HBox getLogs(List<Pair<String, String>> logs) {
        HBox hBox =new HBox();
        List<LogsPresenter> logsPresenters=logs.stream().
                map(stringStringPair -> new LogsPresenter(stringStringPair.getValue(),stringStringPair.getKey()))
                .collect(Collectors.toList());
        addLogsTable(hBox, logsPresenters);

        hBox.setSpacing(5);
        hBox.setPrefHeight(Region.USE_COMPUTED_SIZE);

        return hBox;
    }

    private static void addLogsTable(HBox hBox, List<LogsPresenter> logsPresenters) {
        TableView<LogsPresenter> tableView=new TableView<>();
        TableColumn<LogsPresenter, String> logsTime=new TableColumn<>("Time");
        TableColumn<LogsPresenter, String> logsContext=new TableColumn<>("Context");
        logsTime.setCellValueFactory(new PropertyValueFactory<LogsPresenter,String>("time"));
        logsContext.setCellValueFactory(new PropertyValueFactory<LogsPresenter,String>("context"));
        tableView.getColumns().addAll(logsTime,logsContext);
        tableView.setItems(FXCollections.observableList(logsPresenters));
        logsContext.setPrefWidth(logsPresenters.stream().mapToInt(log -> log.getContext().length()).max().orElse(0)*7.5);
        logsContext.setMinWidth(70);
        hBox.getChildren().addAll(new Label("Logs :"),tableView);
        tableView.setMinHeight(100);
        tableView.setMaxHeight(150);
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

    public VBox getVbox() {
      return StepVbox;
    }
}
