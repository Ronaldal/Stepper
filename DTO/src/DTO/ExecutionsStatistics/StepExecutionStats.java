package DTO.ExecutionsStatistics;



public class StepExecutionStats {

    private final String stepName;

    private final Integer numOfExecutions;
    private final Long avgTimeOfExecutions;

    public StepExecutionStats(String stepName, Integer numOfExecutions, Long avgTimeOfExecutions) {
        this.stepName = stepName;
        this.numOfExecutions = numOfExecutions;
        this.avgTimeOfExecutions = avgTimeOfExecutions;
    }

    public Integer getNumOfExecutions() {
        return numOfExecutions;
    }

    public Long getAvgTimeOfExecutions() {
        return avgTimeOfExecutions;
    }

    public String getStepName() {
        return stepName;
    }
}
