package JavaFx.Body.FlowStats;

import DTO.ExecutionsStatistics.FlowExecutionStats;

import DTO.ExecutionsStatistics.StepExecutionStats;
import JavaFx.Body.AdminBodyController;

import Utils.Utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;

import static AdminUtils.AdminUtils.FLOW_STATS_REQUEST;
import static AdminUtils.AdminUtils.HTTP_CLIENT;

public class FlowStats {
    @FXML private BorderPane flowStatsBorderPane;
    @FXML private CheckBox showStepsChart;
    @FXML private TableView<FlowExecutionStats> flowStatsTableView;
    @FXML private TableColumn<FlowExecutionStats, String> flowCol;
    @FXML private TableColumn<FlowExecutionStats, String> flowExecutionCountCol;
    @FXML private TableColumn<FlowExecutionStats, String> flowAvgTimeCol;
    @FXML private TableView<StepExecutionStats> stepStatsTableView;
    @FXML private TableColumn<StepExecutionStats, String> stepCol;
    @FXML private TableColumn<StepExecutionStats, String> stepExecutionCountCol;
    @FXML private TableColumn<StepExecutionStats, String> stepAvgTimeCol;
    @FXML private BarChart<String, Number> barChart;

    private List<FlowExecutionStats> flowExecutionStatsList=new ArrayList<>();
    private final BarChart<String, Number> stepBarChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
    private AdminBodyController adminBodyController;

    @FXML
    void changeToStepChart(ActionEvent event) {
        if(showStepsChart.isSelected()){
            setStepChart(flowStatsTableView.getSelectionModel().getSelectedItem().getFlowName());
            flowStatsBorderPane.centerProperty().setValue(stepBarChart);
        }
        else {
            setFlowChart();
            flowStatsBorderPane.centerProperty().setValue(barChart);
        }
    }

    @FXML
    void initialize(){
        initTables();
        flowStatsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if(newValue!= null){
                setStepStatsTableView(newValue.getFlowName());
            }
            else {
                setStepStatsTableView(oldValue.getFlowName());
            }
            showStepsChart.selectedProperty().setValue(false);
        });
        setFlowChart();

    }

    private void initTables() {
        initFlowTableCols();
        initStepTableCol();
    }

    private void initStepTableCol() {
        stepCol.setCellValueFactory(new PropertyValueFactory<>("stepName"));
        stepExecutionCountCol.setCellValueFactory(new PropertyValueFactory<>("numOfExecutions"));
        stepAvgTimeCol.setCellValueFactory(new PropertyValueFactory<>("avgTimeOfExecutions"));
    }

    private void initFlowTableCols() {
        flowCol.setCellValueFactory(new PropertyValueFactory<>("flowName"));
        flowExecutionCountCol.setCellValueFactory(new PropertyValueFactory<>("numOfExecutions"));
        flowAvgTimeCol.setCellValueFactory(new PropertyValueFactory<>("avgTimeOfExecutions"));
    }

    public void initStats(List<String> flowNames){
        flowStatsTableView.getItems().clear();
        stepStatsTableView.getItems().clear();
        XYChart.Series<String, Number> flowSeries = new XYChart.Series<>();
        for(String flowName:flowNames){
            FlowExecutionStats flowExecutionStats = adminBodyController.getFlowExecutionsStats(flowName);
            flowStatsTableView.getItems().add(flowExecutionStats);
            flowSeries.getData().add(new XYChart.Data<>(flowName, 0));
        }
        barChart.getData().add(flowSeries);
    }

    public void setStepStatsTableView(String flowName){
        if(stepStatsTableView.getItems()!= null)
            stepStatsTableView.getItems().clear();
        FlowExecutionStats flowExecutionsStats = Utils.runSync(FLOW_STATS_REQUEST.getFlowRequest(flowName),FlowExecutionStats.class,HTTP_CLIENT);
        flowExecutionsStats.getStepExecutionsStats().forEach(step -> {
            stepStatsTableView.getItems().add(step);
        });
    }

    public void setMainController(AdminBodyController adminBodyController){
        this.adminBodyController = adminBodyController;
    }

    public void setFlowChart(){
        barChart.getXAxis().setLabel("Flow");
        barChart.getYAxis().setLabel("Average execution time");
        barChart.setTitle("Flows stats");
    }

    public void setStepChart(String flowName){
        stepBarChart.getData().clear();
        stepBarChart.layout();
        FlowExecutionStats flowExecutionStats = Utils.runSync(FLOW_STATS_REQUEST.getFlowRequest(flowName),FlowExecutionStats.class,HTTP_CLIENT);
        XYChart.Series<String, Number> stepSeries = new XYChart.Series<>();
        for(StepExecutionStats step: flowExecutionStats.getStepExecutionsStats()){
            stepSeries.getData().add(new XYChart.Data<>(step.getStepName(), step.getAvgTimeOfExecutions()));
        }
        stepBarChart.setTitle(flowName);
        stepBarChart.getXAxis().setLabel("Step");
        stepBarChart.getYAxis().setLabel("Average execution time");
        stepBarChart.getData().add(0, stepSeries);
    }

    public void setFlowExecutionStatsList(List<FlowExecutionStats> flowExecutionStatsList) {
        this.flowExecutionStatsList = flowExecutionStatsList;
        Platform.runLater(this::updateStats);
    }

    public void updateStats(){
        if(!flowExecutionStatsList.isEmpty()) {
            int selectedIndex = flowStatsTableView.getSelectionModel().getSelectedIndex();
            flowStatsTableView.setItems(FXCollections.observableList(flowExecutionStatsList));
            flowStatsTableView.getSelectionModel().select(selectedIndex);
            ObservableList<XYChart.Series<String, Number>> seriesList = barChart.getData();
            if (!seriesList.isEmpty()) {
                XYChart.Series<String, Number> series = seriesList.get(0);
                for (XYChart.Data<String, Number> data : series.getData()) {
                    for(FlowExecutionStats flowExecutionStats:flowExecutionStatsList)
                    if (data.getXValue().equals(flowExecutionStats.getFlowName())) {
                        data.setYValue(flowExecutionStats.getAvgTimeOfExecutions());
                    }
                }
            }
        }
    }

}