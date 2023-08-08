package DTO.FlowExecutionData;

import StepperEngine.Flow.execute.FlowExecution;
import StepperEngine.Flow.execute.FlowExecutionWithUser;
import StepperEngine.Flow.execute.FlowStatus;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * holds data of a certain execution of a flow.
 * the data is for presentation only and is not related directly to the main objects that the flow has created,
 * nor the flow itself.
 */
public class FlowExecutionData implements Serializable {
    private final String flowName;
    private final String userExecuted;
    private final String manager;
    private final String uuid;
    private final String executionTime;
    private final String executionResult;
    private final Long executionDuration;
    private final List<StepExecuteData> stepExecuteDataList;
    private final Map<String, StepExecuteData> stepExecuteDataMap;
    private String formattedStartTime;
    private final Set<IOData> freeInputs = new HashSet<>();
    private final Set<IOData> outputs = new HashSet<>();
    private final Map<String, IOData> freeInputsMap =new HashMap<>();
    private final Map<String, IOData> outputsMap = new HashMap<>();
    private final boolean hasContinuation;
    private final Set<IOData> formalOutputs;

    public FlowExecutionData(FlowExecution flowExecution){
        flowName = flowExecution.getFlowDefinition().getName();
        uuid = flowExecution.getUUID();
        executionTime = flowExecution.getTotalTimeInFormat();
        executionDuration = flowExecution.getTotalTime().toMillis();
        executionResult = FlowStatus.getAsString(flowExecution.getFlowExecutionResult());
        stepExecuteDataList = flowExecution.getStepsData().stream()
                .map(step -> {
                    try {
                        return new StepExecuteData(step);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        stepExecuteDataMap = stepExecuteDataList.stream()
                        .collect(Collectors.toMap(StepExecuteData::getFinalName, data->data));
        setFreeInputs(flowExecution);
        setOutputs(flowExecution);
        formalOutputs = getFormalOutputs(flowExecution);
        formattedStartTime=flowExecution.getFormattedStartTime();
        hasContinuation=flowExecution.getFlowDefinition().hasContinuation();
        if(flowExecution instanceof FlowExecutionWithUser){
            userExecuted = ((FlowExecutionWithUser) flowExecution).getUserExecuting();
            manager=  (((FlowExecutionWithUser) flowExecution).getIsManager() ? "manager" : "regular user");
        }else {
            userExecuted = null;
            manager=null;
        }
    }

    public String getUserExecuted() {
        return userExecuted;
    }
    public String getManager() {
        return manager;
    }

    public boolean isHasContinuation() {
        return hasContinuation;
    }

    public String getFormattedStartTime() {
        return formattedStartTime;
    }

    /**
     * a static method of the flowExecutionData class.
     * his purpose is to return a new instance of execution data, if it has been executed.
     * if it has not been executed, returns empty Optional.
     * @param flowExecution the execution to extract the data from
     * @return a data instance of the execution, or null if not executed.
     */
    public static Optional<FlowExecutionData> newInstance(FlowExecution flowExecution) {
        if (flowExecution.hasExecuted()) {
            return Optional.of(new FlowExecutionData(flowExecution));
        } else {
            return Optional.empty();
        }
    }

    public Map<String, IOData> getOutputsMap() {
        return outputsMap;
    }

    public Map<String, IOData> getFreeInputsMap() {
        return freeInputsMap;
    }

    /**
     * sets the formal outputs of the flow execution
     * @param flowExecution a flow that has been executed.
     * @return set of the formal outputs data of the flow
     */
    private Set<IOData> getFormalOutputs(FlowExecution flowExecution) {
        return outputs.stream()
                .filter(output -> flowExecution.getFormalOutputs().containsKey(output.getName()))
                .collect(Collectors.toSet());
    }


    public List<StepExecuteData> getStepExecuteDataList() {
        return stepExecuteDataList;
    }

    /**
     * sets the free inputs data of the flow execution
     * @param flowExecution a flow that has been executed.
     *
     */
    private void setFreeInputs(FlowExecution flowExecution) {
        flowExecution.getFreeInputs().stream()
                .map(data -> {
                    String content;
                    Object value = flowExecution.getInputValue(data.getFullQualifiedName(), data.dataDefinition().getType());
                    if (value == null){
                        content = "not provided";
                    }
                    else {
                        content = value.toString();
                    }
                    return new IOData(
                            false,
                            data.getAliasName(),
                            data.userString(),
                            data.dataDefinition().getType().getSimpleName(),
                            content,
                            String.valueOf(data.necessity()),value, data.getFullQualifiedName());
                })
                .forEach(input -> {
                    freeInputs.add(input);
                    freeInputsMap.put(input.getName(), input);
                });
    }

    /**
     * sets the outputs of the flow execution
     * @param flowExecution a flow that has been executed.
     *
     */
    private void setOutputs(FlowExecution flowExecution) {
        flowExecution.getOutputs().stream()
                .map(data -> {
                    String content;
                    Object value = flowExecution.getOneOutput(data.getFullQualifiedName(), data.dataDefinition().getType());
                    if (value == null){
                        content = "not provided";
                    }
                    else {
                        content = value.toString();
                    }
                    return new IOData(
                            true,
                            data.getAliasName(),
                            data.userString(),
                            data.dataDefinition().getType().getSimpleName(),
                            content, String.valueOf(data.necessity()),
                            value, data.getFullQualifiedName());
                })
                .forEach(output -> {
                    outputs.add(output);
                    outputsMap.put(output.getName(), output);
                });
    }


    public Set<IOData> getFreeInputs() {
        return freeInputs;
    }


    public String getFlowName() {
        return flowName;
    }


    public String getUniqueExecutionId() {
        return uuid;
    }


    public String getExecutionTime() {
        return executionTime;
    }


    public String getFlowExecutionFinalResult() {
        return executionResult;
    }

    public Long getFlowExecutionDuration() {
        return executionDuration;
    }


    public Set<IOData> getOutputs() {
        return outputs;
    }


    public Set<IOData> getFormalOutputs() {
        return formalOutputs;
    }


    public StepExecuteData getStepData(String stepName) {
        return stepExecuteDataMap.get(stepName);
    }

    public String getExecutionResult() {
        return executionResult;
    }
}

