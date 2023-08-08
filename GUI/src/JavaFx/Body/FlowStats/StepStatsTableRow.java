package JavaFx.Body.FlowStats;



import DTO.ExecutionsStatistics.StepExecutionStats;

public class StepStatsTableRow {

    private final String stepName;

    private final Integer numOfExecutions;
    private final Long avgTimeOfExecutions;

    public StepStatsTableRow(StepExecutionStats stepExecutionStats) {
        this.stepName = stepExecutionStats.getStepName();
        this.numOfExecutions = stepExecutionStats.getNumOfExecutions();
        this.avgTimeOfExecutions = stepExecutionStats.getAvgTimeOfExecutions();
    }

    public String getStepName() {
        return stepName;
    }

    public Integer getNumOfExecutions() {
        return numOfExecutions;
    }

    public Long getAvgTimeOfExecutions() {
        return avgTimeOfExecutions;
    }
}
