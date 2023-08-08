package JavaFx.Body.FlowStats;


import DTO.ExecutionsStatistics.FlowExecutionStats;
import DTO.ExecutionsStatistics.StepExecutionStats;
import JavaFx.Body.BodyController;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
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

import java.util.List;

public class FlowStats {
    @FXML
    private BorderPane flowStatsBorderPane;
    @FXML
    private CheckBox showStepsChart;
    @FXML
    private TableView<FlowStatsTableRow> flowStatsTableView;

    @FXML
    private TableColumn<FlowStatsTableRow, String> flowCol;

    @FXML
    private TableColumn<FlowStatsTableRow, String> flowExecutionCountCol;

    @FXML
    private TableColumn<FlowStatsTableRow, String> flowAvgTimeCol;

    @FXML
    private TableView<StepStatsTableRow> stepStatsTableView;

    @FXML
    private TableColumn<StepStatsTableRow, String> stepCol;

    @FXML
    private TableColumn<StepStatsTableRow, String> stepExecutionCountCol;

    @FXML
    private TableColumn<StepStatsTableRow, String> stepAvgTimeCol;

    @FXML
    private BarChart<String, Number> barChart;

    private final BarChart<String, Number> stepBarChart = new BarChart<>(new CategoryAxis(), new NumberAxis());

    private BodyController bodyController;

    ChangeListener<FlowStatsTableRow> switchChartListener = (observable, oldValue, newValue) -> {
        if(newValue != null && showStepsChart.isSelected()) {
            switchToStepsChart(newValue);
        }
    };

    private void switchToStepsChart(FlowStatsTableRow newValue) {
        setStepChart(newValue.getFlowName());
        flowStatsBorderPane.centerProperty().setValue(stepBarChart);

    }


    @FXML
    void initialize(){
        initTables();
        flowStatsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            setStepStatsTableView(newValue.getFlowName());
            showStepsChart.selectedProperty().setValue(false);
        });
        showStepsChart.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue && !flowStatsTableView.getSelectionModel().isEmpty()){
                switchToStepsChart(flowStatsTableView.getSelectionModel().getSelectedItem());
            }
            else if(!newValue){
                flowStatsBorderPane.centerProperty().setValue(barChart);
            }
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
            FlowExecutionStats flowExecutionStats = bodyController.getStepper().getFlowExecutionsStats(flowName);
            flowStatsTableView.getItems().add(new FlowStatsTableRow(flowExecutionStats));
            flowSeries.getData().add(new XYChart.Data<>(flowName, 0));
        }
        barChart.getData().add(flowSeries);
    }


    public void setStepStatsTableView(String flowName){
        stepStatsTableView.getItems().clear();
        FlowExecutionStats flowExecutionsStats = bodyController.getStepper().getFlowExecutionsStats(flowName);
        flowExecutionsStats.getStepExecutionsStats().forEach(step -> {
            StepStatsTableRow stepStatsTableRow = new StepStatsTableRow(step);
            stepStatsTableView.getItems().add(stepStatsTableRow);
        });
    }

    public void setMainController(BodyController bodyController){
        this.bodyController = bodyController;
    }

    public void setFlowChart(){
        barChart.getXAxis().setLabel("Flow");
        barChart.getYAxis().setLabel("Average execution time");
        barChart.setTitle("Flows stats");
    }

    public void setStepChart(String flowName){
        stepBarChart.getData().clear();
        stepBarChart.layout();
        FlowExecutionStats flowExecutionStats = bodyController.getStepper().getFlowExecutionsStats(flowName);
        XYChart.Series<String, Number> stepSeries = new XYChart.Series<>();
        for(StepExecutionStats step: flowExecutionStats.getStepExecutionsStats()){
            stepSeries.getData().add(new XYChart.Data<>(step.getStepName(), step.getAvgTimeOfExecutions()));
        }
        stepBarChart.setTitle(flowName);
        stepBarChart.getXAxis().setLabel("Step");
        stepBarChart.getYAxis().setLabel("Average execution time");
        stepBarChart.getData().add(0, stepSeries);
    }


    public void updateStats(String flowName){
        boolean found = false;
        ObservableList<FlowStatsTableRow> flowStatsTableRows = flowStatsTableView.getItems();
        FlowStatsTableRow newFlowStats = new FlowStatsTableRow(bodyController.getStepper().getFlowExecutionsStats(flowName));
        int numOfFlows = flowStatsTableRows.size();
        for(int i = 0; i< numOfFlows && !found; i++){
            if(flowStatsTableRows.get(i).getFlowName().equals(flowName)){
                flowStatsTableView.getItems().set(i, newFlowStats);
                found = true;
            }
        }
        if(!found){
            flowStatsTableView.getItems().add(newFlowStats);
        }
        ObservableList<XYChart.Series<String, Number>> seriesList = barChart.getData();
        if(!seriesList.isEmpty()){
            XYChart.Series<String, Number> series = seriesList.get(0);
            for(XYChart.Data<String, Number> data : series.getData()){
                if(data.getXValue().equals(flowName)){
                    data.setYValue(newFlowStats.getAvgTimeOfExecutions());
                }
            }
        }
    }

}