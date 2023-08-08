package StepperEngine.Flow.execute.context;

import StepperEngine.Flow.api.FlowDefinition;
import StepperEngine.Flow.api.StepUsageDecleration;
import StepperEngine.Flow.execute.FlowExecution;
import StepperEngine.Flow.execute.StepData.StepExecuteData;
import StepperEngine.Step.api.DataDefinitionsDeclaration;
import StepperEngine.Step.api.StepStatus;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/***
 * The purpose of the department is to link system definition to system execution.
 * The department saves all the input and output information and updates each step with the required information when it is invoked.
 */
public class StepExecutionContextClass implements StepExecutionContext {

    Map<String, DataDefinitionsDeclaration> dataTypes = new HashMap<>();//saves the data types of the input and outputs
    Map<String, Object> dataValues = new HashMap<>();//map name of data definition to in\output to the value
    Map<String, String> customMapping = new HashMap<>();
    Map<String, StepExecuteData> stepExecuteDataMap=new HashMap<>();//



    public StepExecutionContextClass(FlowExecution flow) {
        getDataTypes(flow);
    }

    private void getDataTypes(FlowExecution flowExecution) {
        updateDataTypes(flowExecution);
        storeFreeInputs(flowExecution.getFreeInputsValue());
        storeInitialInputs(flowExecution.getInitialInputsValue());
    }

    private void storeInitialInputs(Map<String, Object> initialInputsValue) {
        for(String dataName:initialInputsValue.keySet()){
            dataValues.put(dataName, initialInputsValue.get(dataName));
        }
    }

    /***
     * Gets the value of the free input and update it in storeValue map
     * @param freeInputsValue
     */
    private void storeFreeInputs(Map<String,Object> freeInputsValue){
        for(String dataName:freeInputsValue.keySet())
        {
            storeValue(dataName,freeInputsValue.get(dataName));
        }
    }

    private void updateDataTypes(FlowExecution flowExecution) {
        FlowDefinition flow= flowExecution.getFlowDefinition();
        for (StepUsageDecleration currStep : flow.getSteps()) {
            for (DataDefinitionsDeclaration dd : currStep.getStepDefinition().getInputs()) {
                dataTypes.put(dd.getFullQualifiedName(), dd);
            }
            for (DataDefinitionsDeclaration dd : currStep.getStepDefinition().getOutputs()) {
                dataTypes.put(dd.getFullQualifiedName(), dd);
            }
        }
    }

    @Override
    public Map<String, Object> getAllData() {
        return dataValues;
    }

    /***
     * Map by custom map of the current step.
     * the step input name is the key and the value is from where we need to take it.
     * @param currStep
     */
    @Override
    public void updateCustomMap(StepUsageDecleration currStep) {
        if (!currStep.getDataMap().isEmpty()) {
            currStep.getDataMap().forEach((s, stringStringPair) -> customMapping.put(s, stringStringPair.getValue()));
        }
    }

    /***
     * @param dataName name of in\output that we want to get
     * @param exceptedDataType what is the type of data name.
     * @return return value of data name.
     */
    @Override
    public <T> T getDataValue(String dataName, Class<T> exceptedDataType) {
        DataDefinitionsDeclaration theExeptedDataType = dataTypes.get(dataName);

        if (exceptedDataType.isAssignableFrom(theExeptedDataType.dataDefinition().getType())) {
            Object aValue = dataValues.get(dataName);
            if(aValue==null) {
                String name=customMapping.get(dataName);
                aValue= dataValues.get(name);
                if(aValue!=null)
                    storeValue(dataName,aValue);
            }
            return exceptedDataType.cast(aValue);
        }
        return null;
    }


    @Override
    public boolean storeValue(String dataName, Object value) {
        DataDefinitionsDeclaration theExeptedDataType = dataTypes.get(dataName);
        if (theExeptedDataType == null){
            theExeptedDataType=dataTypes.get(customMapping.get(dataName));
        }
        if (value != null && theExeptedDataType.dataDefinition().getType().isAssignableFrom(value.getClass())
        && !theExeptedDataType.isInitial()) {
            dataValues.put(dataName, value);
            return true;
        }
        return false;
    }

    /***
     * Update's formal outputs value of the flow
     */
    @Override
    public void addFormalOutput(FlowExecution flowExecution) {
        for (String name :flowExecution.getFlowDefinition().getFormalOuputs().keySet()){
            flowExecution.addOutput(name,dataValues.get(name));
        }
    }

    @Override
    public void addStepData(StepUsageDecleration step) {
        stepExecuteDataMap.put(step.getStepFinalName(),new StepExecuteData(step));
    }

    @Override
    public void addLog(String stepName, String log) {
        stepExecuteDataMap.get(stepName).addLog(log);
    }

    @Override
    public void setInvokeSummery(String stepName, String summery) {
        stepExecuteDataMap.get(stepName).setInvokeSummery(summery);
    }

    @Override
    public void setStepStatus(String stepName, StepStatus stepStatus) {
        stepExecuteDataMap.get(stepName).setStepStatus(stepStatus);
    }

    @Override
    public void setTotalTime(String stepName, Duration totalTime) {
        stepExecuteDataMap.get(stepName).setTotalTime(totalTime);
    }

    @Override
    public StepStatus getStepStatus(String stepName) {
        return stepExecuteDataMap.get(stepName).getStepStatus();
    }

    @Override
    public List<StepExecuteData> getStepsData() {
        return new ArrayList<>(stepExecuteDataMap.values().stream().sorted(Comparator.comparingInt(StepExecuteData::getId)).collect(Collectors.toList()));
    }

    @Override
    public void setStartStep(String stepName) {
        stepExecuteDataMap.get(stepName).setStartTime();
    }

    @Override
    public void setEndStep(String stepName) {
        stepExecuteDataMap.get(stepName).setEndTime();
    }
    @Override
    public void addDataToStepData(String stepName, String dataName,String fullQName,boolean isOutput){
        stepExecuteDataMap.get(stepName).addStepData(dataName, dataValues.get(fullQName),isOutput);
    }


}
