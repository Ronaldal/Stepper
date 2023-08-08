package JavaFx.Body.FlowHistory;

import DTO.FlowExecutionData.FlowExecutionData;
import DTO.FlowExecutionData.StepExecuteData;
import JavaFx.Body.BodyController;
import JavaFx.ClientUtils;
import Requester.execution.ExecutionRequestImpl;
import Utils.Utils;

import com.google.gson.reflect.TypeToken;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static Utils.Constants.GSON_INSTANCE;


public class FlowHistory {

    @FXML private TableView<FlowExecutionData> flowsExecutionTable;
    @FXML private TableColumn<FlowExecutionData, String> flowsExecutionsNamesCol;
    @FXML private TableColumn<FlowExecutionData, String> flowsExecutionsTimeCol;
    @FXML private TableColumn<FlowExecutionData, String> flowsExecutionsStatusCol;
    @FXML private Button rerunButton;
    @FXML private ComboBox<String> filterChoose;
    @FXML private Label filterSelectionLabel;
    @FXML private ImageView resetTable;
    @FXML private VBox MainExecutionDataVbox;
    @FXML private TreeView<String> StepsTreeVIew;
    @FXML private Label CentralFlowName;
    @FXML private VBox filterVbox;
    private BooleanProperty booleanProperty=new SimpleBooleanProperty();
    private BodyController bodyController;
    private List<FlowExecutionData> flowExecutionDataList=new ArrayList<>();

    public void setMainController(BodyController bodyController) {
        this.bodyController = bodyController;

    }
    @FXML
    void initialize(){
        filterChoose.setOnAction(event -> {
            setExecutionTableByFilter();
        });
        filterSelectionLabel.textProperty().bind(Bindings
                .when(booleanProperty)
                .then("Selected filter option :")
                .otherwise(
                        "Choose Filter :"
                )
        );
        rerunButton.opacityProperty().set(0.2);
        rerunButton.cursorProperty().set(Cursor.DISAPPEAR);
        disaperRestFilterButton();
        //initContinuationButton();
    }

    public void setFlowExecutionDataList(List<FlowExecutionData> flowExecutionDataList) {
        if(flowExecutionDataList.size()!=this.flowExecutionDataList.size()) {
            this.flowExecutionDataList = flowExecutionDataList;
            Platform.runLater(() -> {
                if (booleanProperty.get()) {
                    setExecutionTableByFilter();
                } else {
                    setFlowsExecutionTable();
                }

            });
        }
    }

    private void setExecutionTableByFilter() {
        String selectedOption = filterChoose.getValue();
        setItemsInFlowsExecutionTable(FXCollections.observableList(flowExecutionDataList.stream().filter
                (flowExecutionData -> flowExecutionData.getExecutionResult().equals(selectedOption)).collect(Collectors.toList())));
        booleanProperty.set(true);
        resetTable.opacityProperty().set(1);
        resetTable.cursorProperty().set(Cursor.HAND);
    }




    private void disaperRestFilterButton() {
        resetTable.opacityProperty().set(0.2);
        resetTable.cursorProperty().set(Cursor.DISAPPEAR);
    }

    @FXML
    void rerunFlow(ActionEvent event) {
        bodyController.rerunFlow(flowsExecutionTable.getSelectionModel().getSelectedItem());
    }


    @FXML
    void restTableFilter(MouseEvent event) {
        if(resetTable.cursorProperty().get().equals(Cursor.HAND)) {
            restTable();
            filterChoose.getSelectionModel().clearSelection();
            setItemsInFlowsExecutionTable(FXCollections.observableList(flowExecutionDataList));
            booleanProperty.set(false);
            disaperRestFilterButton();
        }
    }

    private void restTable(){
        RotateTransition transition=new RotateTransition();
        transition.setNode(resetTable);
        transition.setDuration(Duration.seconds(0.7));
        transition.setCycleCount(1);
        transition.setByAngle(360);
        transition.play();
    }
    public void setFlowsExecutionTable() {
        if (!flowExecutionDataList.isEmpty()) {
            flowsExecutionsNamesCol.setCellValueFactory(new PropertyValueFactory<FlowExecutionData, String>("flowName"));
            flowsExecutionsTimeCol.setCellValueFactory(new PropertyValueFactory<FlowExecutionData, String>("formattedStartTime"));
            flowsExecutionsStatusCol.setCellValueFactory(new PropertyValueFactory<FlowExecutionData, String>("executionResult"));
            setAligmentToFlowsExecutionCols();
            setItemsInFlowsExecutionTable(FXCollections.observableList(flowExecutionDataList));
            filterChoose.setItems(FXCollections.observableList(getOptionList()));
        }
    }

    private void setItemsInFlowsExecutionTable(ObservableList<FlowExecutionData> data) {
        flowsExecutionTable.setItems(data);
    }

    private List<String>  getOptionList() {
        return flowExecutionDataList
                .stream()
                .map(FlowExecutionData::getExecutionResult)
                .filter(name -> !name.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    private void setAligmentToFlowsExecutionCols() {
        flowsExecutionsNamesCol.setCellFactory(column -> new TableCell<FlowExecutionData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                setAlignment(Pos.CENTER);  // Align text to the middle
            }
        });

        flowsExecutionsTimeCol.setCellFactory(column -> new TableCell<FlowExecutionData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                setAlignment(Pos.CENTER);  // Align text to the middle
            }
        });

        flowsExecutionsStatusCol.setCellFactory(column -> new TableCell<FlowExecutionData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                setAlignment(Pos.CENTER);  // Align text to the middle
            }
        });
    }
    @FXML
    void setFlowExecutionDetails(MouseEvent event) {
        if(event.getClickCount()==2){
            FlowExecutionData selectedItem = flowsExecutionTable.getSelectionModel().getSelectedItem();
            CentralFlowName.setText("Flow Selected :"+selectedItem.getFlowName());
            MainExecutionDataVbox.getChildren().clear();
            MainExecutionDataVbox.getChildren().add(bodyController.getFlowExecutionData(selectedItem).getVbox());

            TreeItem root=new TreeItem(selectedItem.getFlowName(),bodyController.getExecutionStatusImage(selectedItem.getExecutionResult()));
            StepsTreeVIew.setRoot(root);
            for(StepExecuteData step:selectedItem.getStepExecuteDataList()){
                TreeItem<String> childItem = new TreeItem<>(step.getFinalName(),bodyController.getExecutionStatusImage(step.getStepStatus().toString()));
                root.getChildren().add(childItem);
            }
            rerunButton.opacityProperty().set(1);
            rerunButton.cursorProperty().set(Cursor.HAND);
            //addContinuation(selectedItem);
        }
    }



    @FXML
    void setStepData(MouseEvent event) {
        TreeItem<String> selectedItem = StepsTreeVIew.getSelectionModel().getSelectedItem();
        if(selectedItem!=null){
            MainExecutionDataVbox.getChildren().clear();
            boolean isRoot = selectedItem.getParent() == null;
            if (isRoot)
                MainExecutionDataVbox.getChildren().add(bodyController.getFlowExecutionData(flowsExecutionTable.getSelectionModel().getSelectedItem()).getVbox());
            else {
                MainExecutionDataVbox.getChildren().add(bodyController.getStepExecutionData(flowsExecutionTable.getSelectionModel().getSelectedItem(), selectedItem.getValue()));
            }
        }
    }



}
