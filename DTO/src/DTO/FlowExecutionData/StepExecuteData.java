package DTO.FlowExecutionData;

import StepperEngine.Step.api.StepStatus;
import javafx.util.Pair;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class StepExecuteData {

    private final String finalName;
    private final String name;
    private Duration totalTime;
    private String invokeSummery;
    private StepStatus stepStatus;
    private List<Pair<String, String>> logs;
    private final Integer id;
    private LocalDateTime startTime;
    private String formattedStartTime;
    private LocalDateTime endTime;
    private String formattedEndTime;
    private Map<String, Object> dataMap = new HashMap<>();
    private Set<IOData> inputs =new HashSet<>();
    private Set<IOData> outputs = new HashSet<>();

    public StepExecuteData(StepperEngine.Flow.execute.StepData.StepExecuteData step) throws IllegalAccessException {
        this.id = step.getId();
        this.name = step.getName();
        this.finalName = step.getFinalName();
        this.totalTime = step.getTotalTime();
        this.dataMap = step.getDataMap();
        this.logs = step.getLogs();
        this.endTime = step.getEndTime();
        this.startTime = step.getStartTime();
        this.formattedEndTime = step.getFormattedEndTime();
        this.formattedStartTime = step.getFormattedStartTime();
        this.inputs = step.getInputs();
        this.outputs = step.getOutputs();
        this.invokeSummery = step.getInvokeSummery();
        this.stepStatus = step.getStepStatus();
    }

    public String getFinalName() {
        return finalName;
    }

    public String getName() {
        return name;
    }

    public Duration getTotalTime() {
        return totalTime;
    }

    public String getInvokeSummery() {
        return invokeSummery;
    }

    public StepStatus getStepStatus() {
        return stepStatus;
    }

    public List<Pair<String, String>> getLogs() {
        return logs;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getFormattedStartTime() {
        return formattedStartTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getFormattedEndTime() {
        return formattedEndTime;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public Set<IOData> getInputs() {
        return inputs;
    }

    public Set<IOData> getOutputs() {
        return outputs;
    }
}
