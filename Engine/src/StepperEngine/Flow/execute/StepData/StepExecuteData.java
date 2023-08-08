package StepperEngine.Flow.execute.StepData;


import DTO.FlowExecutionData.IOData;
import StepperEngine.Flow.api.StepUsageDecleration;
import StepperEngine.Step.api.DataDefinitionsDeclaration;
import StepperEngine.Step.api.StepStatus;
import javafx.util.Pair;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StepExecuteData implements Serializable {

    private final String finalName;
    private final String name;
    private Duration totalTime;
    private String invokeSummery;
    private StepStatus stepStatus;
    private List<Pair<String, String>> logs=new LinkedList<>();
    private final Integer id;
    private LocalDateTime startTime;
    private String formattedStartTime="step has not started";
    private LocalDateTime endTime;
    private String formattedEndTime="step has not ended";
    private Map<String, Object> dataMap = new HashMap<>();
    private Set<IOData> inputs =new HashSet<>();
    private Set<IOData> outputs = new HashSet<>();
    private List<DataDefinitionsDeclaration> inputsDD;
    private List<DataDefinitionsDeclaration> outputsDD;


    public StepExecuteData(StepUsageDecleration step) {
        this.finalName =step.getStepFinalName();
        this.name=step.getStepDefinition().getName();
        this.id=step.getIndex();
        inputsDD =step.getStepDefinition().getInputs();
        outputsDD =step.getStepDefinition().getOutputs();
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setStartTime() {
        this.startTime = LocalDateTime.now();
        formattedStartTime= getFormattedTime(startTime);
    }

    public Integer getId() {
        return id;
    }
    public String getFinalName() {
        return finalName;
    }


    public Duration getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Duration totalTime) {
        this.totalTime = totalTime;
    }

    public String getInvokeSummery() {
        return invokeSummery;
    }

    public void setInvokeSummery(String invokeSummery) {
        this.invokeSummery = invokeSummery;
    }

    public StepStatus getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(StepStatus stepStatus) {
        this.stepStatus = stepStatus;
    }

    public List<Pair<String, String>> getLogs() {
        return logs;
    }

    public void addLog(String log) {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        String formattedTime = currentTime.format(formatter);
        this.logs.add(new Pair<>(log,formattedTime));
    }

    public void setEndTime() {
        this.endTime = LocalDateTime.now();
        formattedEndTime= getFormattedTime(endTime);
    }

    private String getFormattedTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return time.format(formatter);
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() throws IllegalAccessException{
        if(endTime!=null){
            return endTime;
        }
        throw new IllegalAccessException("step has not ended");
    }

    public String getFormattedStartTime() {
        return formattedStartTime;
    }

    public String getFormattedEndTime() {
        return formattedEndTime;
    }

    public void addStepData(String dataName, Object value,boolean isOutput){
        dataMap.put(dataName, value);
        if(isOutput){
            updateDataMaps(dataName, value, true, outputsDD, outputs);
        }else {
            updateDataMaps(dataName, value, false, inputsDD, inputs);
        }

    }

    private boolean updateDataMaps(String dataName, Object value,Boolean isOutput, List<DataDefinitionsDeclaration> dataDefinitionsDeclarationList,Set<IOData> ioDataSet) {
        String content;
        if (value == null){
            content = "not provided";
        }
        else {
            content = value.toString();
        }
        for(DataDefinitionsDeclaration data:dataDefinitionsDeclarationList){
            if(data.getAliasName().equals(dataName)){
                ioDataSet.add(new IOData(isOutput, dataName,data.userString(),
                        data.dataDefinition().getType().getSimpleName(), content,data.necessity().toString(), value, data.getFullQualifiedName()));
                return true;
            }
        }
        return false;
    }
    public void setStepData(Map<String, Object> allData){
        for(String dataName:allData.keySet()){
            if(!updateDataMaps(dataName,allData.get(dataName),false,inputsDD,inputs))
                updateDataMaps(dataName,allData.get(dataName),true,outputsDD,outputs);
        }
    }
    public Set<IOData> getInputs() {
        return inputs;
    }

    public Set<IOData> getOutputs() {
        return outputs;
    }
}
