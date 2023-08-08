package JavaFx.Body.FlowHistory;


import DTO.FlowExecutionData.FlowExecutionData;
import DTO.FlowExecutionData.StepExecuteData;
import JavaFx.Body.AdminBodyController;


import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class FlowHistory {

    @FXML private TableView<FlowExecutionData> flowsExecutionTable;
    @FXML private TableColumn<FlowExecutionData, String> flowsExecutionsNamesCol;
    @FXML private TableColumn<FlowExecutionData, String> flowsExecutionsTimeCol;
    @FXML private TableColumn<FlowExecutionData, String> flowsExecutionsStatusCol;
    @FXML private TableColumn<FlowExecutionData, String> UserNameCol;
    @FXML private TableColumn<FlowExecutionData, String> managerCol;
    @FXML private ComboBox<String> filterChoose;
    @FXML private Label filterSelectionLabel;
    @FXML private ImageView resetTable;
    @FXML private VBox MainExecutionDataVbox;
    @FXML private TreeView<String> StepsTreeVIew;
    @FXML private Label CentralFlowName;
    @FXML private VBox filterVbox;

    private BooleanProperty booleanProperty=new SimpleBooleanProperty();
    private AdminBodyController adminBodyController;
    private List<FlowExecutionData> flowExecutionDataList=new ArrayList<>();


    public void setMainController(AdminBodyController adminBodyController) {
        this.adminBodyController = adminBodyController;

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
        disaperRestFilterButton();
    }

    private void setExecutionTableByFilter() {
        String selectedOption = filterChoose.getValue();
        setItemsInFlowsExecutionTable(FXCollections.observableList(flowExecutionDataList.stream().filter
                (flowExecutionData -> flowExecutionData.getExecutionResult().equals(selectedOption)).collect(Collectors.toList())));
        booleanProperty.set(true);
        resetTable.opacityProperty().set(1);
        resetTable.cursorProperty().set(Cursor.HAND);
    }

    public void setFlowExecutionDataList(List<FlowExecutionData> flowExecutionDataList) {
        if(flowExecutionDataList.size()==1 && !adminBodyController.getStatsRefresherIn()){
            adminBodyController.setStatsRefresherIn(true);
            adminBodyController.updateStats();
        }
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

    private void disaperRestFilterButton() {
        resetTable.opacityProperty().set(0.2);
        resetTable.cursorProperty().set(Cursor.DISAPPEAR);
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
            UserNameCol.setCellValueFactory(new PropertyValueFactory<FlowExecutionData,String>("userExecuted"));
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
        UserNameCol.setCellFactory(column -> new TableCell<FlowExecutionData, String>() {
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
            MainExecutionDataVbox.getChildren().add(adminBodyController.getFlowExecutionData(selectedItem).getVbox());

            TreeItem root=new TreeItem(selectedItem.getFlowName(), adminBodyController.getExecutionStatusImage(selectedItem.getExecutionResult()));
            StepsTreeVIew.setRoot(root);
            for(StepExecuteData step:selectedItem.getStepExecuteDataList()){
                TreeItem<String> childItem = new TreeItem<>(step.getFinalName(), adminBodyController.getExecutionStatusImage(step.getStepStatus().toString()));
                root.getChildren().add(childItem);
            }
        }
    }

    @FXML
    void setStepData(MouseEvent event) {
        TreeItem<String> selectedItem = StepsTreeVIew.getSelectionModel().getSelectedItem();
        if(selectedItem!=null){
            MainExecutionDataVbox.getChildren().clear();
            boolean isRoot = selectedItem.getParent() == null;
            if (isRoot)
                MainExecutionDataVbox.getChildren().add(adminBodyController.getFlowExecutionData(flowsExecutionTable.getSelectionModel().getSelectedItem()).getVbox());
            else {
                MainExecutionDataVbox.getChildren().add(adminBodyController.getStepExecutionData(flowsExecutionTable.getSelectionModel().getSelectedItem(), selectedItem.getValue()));
            }
        }
    }


//    @FXML
//    void continueToFlow(MouseEvent event) {
//        adminBodyController.applyContinuationFromHistoryTab(flowsExecutionTable.getSelectionModel().getSelectedItem().getUniqueExecutionId(),
//                continuationChoiceBox.getValue());
//    }

    @FXML
    void setContinuationText(MouseEvent event) {
//        Tooltip tooltip = new Tooltip("Choose flow to continue");
//        tooltip.setAutoHide(true);
//        tooltip.show(continuationChoiceBox, event.getScreenX(), event.getScreenY());
//        continuationChoiceBox.setTooltip(tooltip);
//        PauseTransition pause = new PauseTransition(Duration.seconds(1));
//        pause.setOnFinished(eventa -> {
//            tooltip.hide();
//            continuationChoiceBox.setTooltip(null);
//        });
//        pause.play();
    }


}
