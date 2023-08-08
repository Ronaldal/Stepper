package StepperEngine.Flow.execute.context;

import StepperEngine.Flow.api.StepUsageDecleration;
import StepperEngine.Flow.execute.FlowExecution;
import StepperEngine.Flow.execute.StepData.StepExecuteData;
import StepperEngine.Step.api.StepStatus;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> exceptedDataType);
    boolean storeValue(String dataName, Object value);

    void updateCustomMap(StepUsageDecleration currStep);

    Map<String, Object> getAllData();

    void addFormalOutput(FlowExecution flowExecution);

    //void stepData(StepExecuteData name);
    void addLog(String stepName, String log);

    void setInvokeSummery(String stepName, String summery);

    void setStepStatus(String stepName, StepStatus stepStatus);

    void setTotalTime(String stepName, Duration totalTime);

    void addStepData(StepUsageDecleration step);

    void setStartStep(String stepName);

    void setEndStep(String stepName);

    StepStatus getStepStatus(String stepName);

    List<StepExecuteData> getStepsData();

    public void addDataToStepData(String stepName, String dataName,String fullQName,boolean isOutput);
}