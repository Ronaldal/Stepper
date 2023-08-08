package JavaFx.Body.FlowHistory;

import DTO.FlowExecutionData.FlowExecutionData;
import DTO.FlowExecutionData.StepExecuteData;
import JavaFx.Body.BodyController;


import javafx.animation.RotateTransition;
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
import java.util.List;
import java.util.stream.Collectors;


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
    @FXML private ChoiceBox<String> continuationChoiceBox;
    @FXML private ImageView continuationButtonImage;
    private BooleanProperty booleanProperty=new SimpleBooleanProperty();
    private BodyController bodyController;

    public void setMainController(BodyController bodyController) {
        this.bodyController = bodyController;

    }
    @FXML
    void initialize(){
        filterChoose.setOnAction(event -> {
            String selectedOption = filterChoose.getValue();
            setItemsInFlowsExecutionTable(FXCollections.observableList(bodyController.getStepper().getFlowExecutionDataList()
                    .stream().filter(flowExecutionData -> flowExecutionData.getExecutionResult().equals(selectedOption)).collect(Collectors.toList())));
            booleanProperty.set(true);
            resetTable.opacityProperty().set(1);
            resetTable.cursorProperty().set(Cursor.HAND);
        });
        filterSelectionLabel.textProperty().bind(Bindings
                .when(booleanProperty)
                .then("Selected filter option :")
                .otherwise(
                        "Choose Filter :"
                )
        );
        continuationChoiceBox.setOnAction(this::startContinuation);
        rerunButton.opacityProperty().set(0.2);
        rerunButton.cursorProperty().set(Cursor.DISAPPEAR);
        disaperRestFilterButton();
        initContinuationButton();
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
            setItemsInFlowsExecutionTable(FXCollections.observableList(bodyController.getStepper().getFlowExecutionDataList()));
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
        if (!bodyController.getStepper().getFlowExecutionDataList().isEmpty()) {
            flowsExecutionsNamesCol.setCellValueFactory(new PropertyValueFactory<FlowExecutionData, String>("flowName"));
            flowsExecutionsTimeCol.setCellValueFactory(new PropertyValueFactory<FlowExecutionData, String>("formattedStartTime"));
            flowsExecutionsStatusCol.setCellValueFactory(new PropertyValueFactory<FlowExecutionData, String>("executionResult"));
            setAligmentToFlowsExecutionCols();
            setItemsInFlowsExecutionTable(FXCollections.observableList(bodyController.getStepper().getFlowExecutionDataList()));
            filterChoose.setItems(FXCollections.observableList(getOptionList()));
        }
    }

    private void setItemsInFlowsExecutionTable(ObservableList<FlowExecutionData> data) {
        flowsExecutionTable.setItems(data);
    }

    private List<String>  getOptionList() {
        return bodyController.getStepper().getFlowExecutionDataList()
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
            addContinuation(selectedItem);
        }
    }

    private void addContinuation(FlowExecutionData selectedItem) {
        if(selectedItem.isHasContinuation()){
            continuationChoiceBox.setItems(FXCollections.observableArrayList(bodyController.getStepper().getFlowsDetailsByName(selectedItem.getFlowName()).getContinuationNames()));
        }
        else{
            initContinuationButton();
            if(continuationChoiceBox.getItems()!= null)
                continuationChoiceBox.getItems().clear();
        }
    }

    private void startContinuation(ActionEvent actionEvent) {
        makeContinuationButtonEnabled();
    }
    private void initContinuationButton() {
        continuationButtonImage.opacityProperty().set(0.2);
        continuationButtonImage.cursorProperty().set(Cursor.DISAPPEAR);
    }
    private void makeContinuationButtonEnabled() {
        continuationButtonImage.opacityProperty().set(1);
        continuationButtonImage.cursorProperty().set(Cursor.HAND);
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


    @FXML
    void continueToFlow(MouseEvent event) {
        bodyController.applyContinuationFromHistoryTab(flowsExecutionTable.getSelectionModel().getSelectedItem().getUniqueExecutionId(),
                continuationChoiceBox.getValue());
    }

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
