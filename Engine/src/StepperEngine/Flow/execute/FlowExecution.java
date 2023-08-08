package StepperEngine.Flow.execute;

import StepperEngine.DataDefinitions.Enumeration.MethodEnumerator;
import StepperEngine.DataDefinitions.Enumeration.ProtocolEnumerator;
import StepperEngine.DataDefinitions.Enumeration.ZipEnumerator;
import StepperEngine.Flow.api.FlowDefinition;
import StepperEngine.Flow.execute.StepData.StepExecuteData;
import StepperEngine.Step.api.DataDefinitionsDeclaration;
import StepperEngine.Step.api.DataNecessity;
import javafx.util.Pair;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/***
 * Saves information about execution of flow
 */
public class FlowExecution {

    private final FlowDefinition flowDefinition;
    private final String id;
    private static Integer idCounter = 1;
    private Duration totalTime;
    private FlowStatus flowStatus;
    private String formattedStartTime;
    private boolean hasExecuted = false;
    private String uuidAsString;
    private final Map<String, Object> freeInputsValue = new HashMap<>();
    private final Map<String, Object> initialInputsValue = new HashMap<>();
    private final Map<String, Object> formalOutputs = new HashMap<>();
    private Map<String,Object> allData=new HashMap<>();
    private List<StepExecuteData> stepsData = new LinkedList<>();
    private final Map<String, StepExecuteData> stepsDataMap = new HashMap<>();
    private final Set<DataDefinitionsDeclaration> freeInputs;
    private final Set<DataDefinitionsDeclaration> outputs;

    private final int numOfSteps;
    private int numOfStepsExecuted = 0;
    private boolean canBeExecuted;

    public FlowExecution(FlowDefinition flowDefinition) {
        this.flowDefinition = flowDefinition;
        this.id = idCounter.toString();
        idCounter++;
        freeInputs = flowDefinition.getFreeInputs();
        outputs = flowDefinition.getAllOutputs().values().stream()
                .map(Pair::getKey)
                .collect(Collectors.toSet());
        numOfSteps = flowDefinition.getSteps().size();
        canBeExecuted = freeInputs.size() == 0;
        createUUID();
        addInitialInputs(flowDefinition);
    }

    private void addInitialInputs(FlowDefinition flowDefinition) {
        Map<String, Pair<DataDefinitionsDeclaration, Object>> initialInputs = flowDefinition.getInitialInputs();
        for(String initialInput:initialInputs.keySet()){
            initialInputsValue.put(initialInputs.get(initialInput).getKey().getFullQualifiedName(),initialInputs.get(initialInput).getValue());
        }
    }

    public Map<String, Object> getInitialInputsValue() {
        return initialInputsValue;
    }

    public Map<String, Object> getAllData() {
        return allData;
    }

    public StepExecuteData getStepExecuteData(String stepName){
        return stepsDataMap.get(stepName);
    }


    public void setAllData(Map<String, Object> allData) {
        this.allData = allData;
    }

    public void setStepsData(List<StepExecuteData> stepsData) {
        this.stepsData = stepsData;
        for(StepExecuteData step:stepsData){
            stepsDataMap.put(step.getFinalName(), step);
        }
    }

    public void addStepExecuteData(StepExecuteData stepExecuteData) {
        stepsData.add(stepExecuteData);
        stepsDataMap.put(stepExecuteData.getFinalName(), stepExecuteData);
    }



    public String getFormattedStartTime() {
        return formattedStartTime;
    }

    public void setFormattedStartTime(String formattedStartTime) {
        this.formattedStartTime = formattedStartTime;
    }


    public Duration getTotalTime() {
        return totalTime;
    }

    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

    public String getId() {
        return id;
    }

    public FlowStatus getFlowExecutionResult() {
        return flowStatus;
    }


    public void setFlowStatus(FlowStatus flowStatus) {
        this.flowStatus = flowStatus;
    }

    public void setTotalTime(Duration totalTime) {
        this.totalTime = totalTime;
    }

    public boolean hasExecuted() {
        return hasExecuted;
    }

    public String getTotalTimeInFormat() {
        return String.format("%,d", totalTime.getNano() / 1_000_000);
    }

    public void createUUID() {
        UUID uuid = UUID.randomUUID();
        this.uuidAsString = uuid.toString();
    }

    public String getUUID() {
        return uuidAsString;
    }

    public boolean addFreeInput(String dataName, Object value) {
        if(hasExecuted){
            return false;
        }
        Optional<DataDefinitionsDeclaration> optionalData = flowDefinition.getFreeInputs().stream().filter(input -> input.getAliasName().equals(dataName)).findFirst();
        if(optionalData.isPresent()){
            if(optionalData.get().dataDefinition().getType().isAssignableFrom(value.getClass())){
                return addValueToFreeInputs(optionalData.get().getFullQualifiedName(), value);
            }
            else if(optionalData.get().dataDefinition().getName().equals("Enumerator")){
                if(EnumSet.allOf(ZipEnumerator.class).stream()
                        .map(ZipEnumerator::getStringValue)
                        .anyMatch(zipType-> zipType.equals(value))){
                    return addValueToFreeInputs(optionalData.get().getFullQualifiedName(), ZipEnumerator.fromString(value.toString()));
                }
                if(EnumSet.allOf(MethodEnumerator.class).stream()
                        .map(MethodEnumerator::getStringValue)
                        .anyMatch(methodType-> methodType.equals(value))){
                    return addValueToFreeInputs(optionalData.get().getFullQualifiedName(), MethodEnumerator.fromString(value.toString()));
                }
                if(EnumSet.allOf(ProtocolEnumerator.class).stream()
                        .map(ProtocolEnumerator::getStringValue)
                        .anyMatch(protocolType-> protocolType.equals(value))){
                    return addValueToFreeInputs(optionalData.get().getFullQualifiedName(), ProtocolEnumerator.fromString(value.toString()));
                }
            }
        }
        return false;
    }

    private boolean addValueToFreeInputs(String dataName, Object value) {
        freeInputsValue.put(dataName, value);
        canBeExecuted = freeInputs.stream()
                .filter(data -> data.necessity().equals(DataNecessity.MANDATORY))
                .allMatch(data -> freeInputsValue.containsKey(data.getFullQualifiedName()));
        return true;
    }

    public boolean isCanBeExecuted() {
        return canBeExecuted;
    }


    public Map<String, Object> getFreeInputsValue() {
        return freeInputsValue;
    }

    public void addOutput(String dataName, Object value) {
        formalOutputs.put(dataName, value);
    }

    public Set<DataDefinitionsDeclaration> getOutputs() {
        return outputs;
    }

    //Changed form up to down because we need all outputs
    public <T> T getOneOutput(String dataName, Class<T> exceptedDataType) {
        return allData.containsKey(dataName) ? exceptedDataType.cast(allData.get(dataName)) : null;
    }

    public List<StepExecuteData> getStepsData() {
        return stepsData;
    }

    public Set<DataDefinitionsDeclaration> getFreeInputs() {
        return freeInputs;
    }

    public <T> T getInputValue(String inputName, Class<T> exceptedDataType){
        return freeInputsValue.containsKey(inputName) ? exceptedDataType.cast(freeInputsValue.get(inputName)): null;
    }

    public void setHasExecuted(boolean hasExecuted) {
        this.hasExecuted = hasExecuted;
        canBeExecuted = false;
    }

    public Map<String, Object> getFormalOutputs() {
        return formalOutputs;
    }

    public int getNumOfStepsExecuted() {
        return numOfStepsExecuted;
    }

    public void setNumOfStepsExecuted(int numOfStepsExecuted) {
        this.numOfStepsExecuted = numOfStepsExecuted;
    }

    public int getNumOfSteps() {
        return numOfSteps;
    }

    public void applyContinuation(FlowExecution pastFlow){
        updateFreeInputsValue(pastFlow);
        for(String source:flowDefinition.getContinuationMapping().keySet()){
            freeInputsValue.put(flowDefinition.getContinuationMapping().get(source),pastFlow.allData.get(source));
        }
    }

    public void updateFreeInputsValue(FlowExecution pastFlow) {
        for(DataDefinitionsDeclaration dd:freeInputs){
            if (pastFlow.allData.containsKey(dd.getFullQualifiedName())){
                freeInputsValue.put(dd.getFullQualifiedName(), pastFlow.allData.get(dd.getFullQualifiedName()));
            }
        }
    }
}
