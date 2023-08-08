package DTO.ExecutionsStatistics;



import StepperEngine.Flow.api.FlowDefinition;
import StepperEngine.Flow.api.StepUsageDecleration;
import StepperEngine.Flow.execute.FlowExecution;
import StepperEngine.Flow.execute.StepData.StepExecuteData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowExecutionStats {

    private final String flowName;

    private final Integer numOfExecutions;
    private Long avgTimeOfExecutions;

    private final List<StepExecutionStats> stepExecutionStatisticsList = new ArrayList<>();

    private final Map<String, StepExecutionStats> stepExecutionStatsMap = new HashMap<>();

    public FlowExecutionStats(FlowDefinition flowDefinition, List<FlowExecution> flowExecutions) {
        this.flowName = flowDefinition.getName();
        this.numOfExecutions = flowExecutions != null ? flowExecutions.size() : 0;
        if(numOfExecutions!= 0)
            this.avgTimeOfExecutions = flowExecutions.stream()
                .mapToLong(flowExecution -> flowExecution.getTotalTime().toMillis())
                .sum() / numOfExecutions;
        else
            this.avgTimeOfExecutions = 0L;
        setStepExecutionsStats(flowDefinition, flowExecutions);
    }

    private void setStepExecutionsStats(FlowDefinition flowDefinition, List<FlowExecution> flowExecutions) {
        for (StepUsageDecleration step : flowDefinition.getSteps()){
            String stepName = step.getStepFinalName();
            int stepNumOfExecutions = 0;
            long stepTotalTimeOfExecutions = 0;
            if(flowExecutions != null) {
                for (FlowExecution flowExecution : flowExecutions) {
                    StepExecuteData stepExecuteData = flowExecution.getStepExecuteData(stepName);
                    if (stepExecuteData != null) {
                        stepNumOfExecutions++;
                        stepTotalTimeOfExecutions += stepExecuteData.getTotalTime().toMillis();
                    }
                }
            }
            long stepAvgTimeOfExecutions = stepNumOfExecutions > 0 ? stepTotalTimeOfExecutions/stepNumOfExecutions : 0;
            StepExecutionStats stepExecutionStats = new StepExecutionStats(stepName, numOfExecutions, stepAvgTimeOfExecutions);
            stepExecutionStatisticsList.add(stepExecutionStats);
            stepExecutionStatsMap.put(stepName, stepExecutionStats);
        }
    }




    public Integer getNumOfExecutions() {
        return numOfExecutions;
    }

    public Long getAvgTimeOfExecutions() {
        return avgTimeOfExecutions;
    }


    public String getFlowName() {
        return flowName;
    }


    public List<StepExecutionStats> getStepExecutionsStats() {
        return stepExecutionStatisticsList;
    }


    public StepExecutionStats getStepExecutionStats(String stepName) {
        return stepExecutionStatsMap.get(stepName);
    }
}
