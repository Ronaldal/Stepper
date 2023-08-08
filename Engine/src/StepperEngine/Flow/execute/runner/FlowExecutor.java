package StepperEngine.Flow.execute.runner;

import StepperEngine.Flow.api.StepUsageDecleration;
import StepperEngine.Flow.execute.FlowExecution;
import StepperEngine.Flow.execute.FlowStatus;
import StepperEngine.Flow.execute.context.StepExecutionContext;
import StepperEngine.Flow.execute.context.StepExecutionContextClass;
import StepperEngine.Step.api.DataDefinitionsDeclaration;
import StepperEngine.Step.api.StepStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/***
 * The purpose of the class is to execute different flows each time
 */
public class FlowExecutor {
    /***
     *The function receives an instance of flow that needs to be executed.
     * As part of the execution, we execute each step.
     * @param currFlow flow execution data wich we want to execute
     */
    public void executeFlow(FlowExecution currFlow) {
        StepExecutionContext stepExecutionContext = new StepExecutionContextClass(currFlow);
        FlowStatus flowStatus = FlowStatus.SUCCESS;
        Instant start = updateTime(currFlow);
        for (StepUsageDecleration step : currFlow.getFlowDefinition().getSteps()) {
            flowStatus = executeStep(currFlow, stepExecutionContext, flowStatus, step);
            if (flowStatus == null) break;
            currFlow.setNumOfStepsExecuted(currFlow.getNumOfStepsExecuted() + 1);
        }
        finishExecution(currFlow, stepExecutionContext, flowStatus, start);
    }

    private static FlowStatus executeStep(FlowExecution currFlow, StepExecutionContext stepExecutionContext, FlowStatus flowStatus, StepUsageDecleration step) {
        startStep(stepExecutionContext, step);
        StepStatus stepStatus = invokeStep(stepExecutionContext, step);
        stepExecutionContext.setEndStep(step.getStepFinalName());
        addDataToStep(stepExecutionContext, step);
        flowStatus = endStep(flowStatus, step, stepStatus);
        if (flowStatus == null) return null;
        return flowStatus;
    }

    private static void addDataToStep(StepExecutionContext stepExecutionContext, StepUsageDecleration step) {
        for(DataDefinitionsDeclaration data: step.getStepDefinition().getInputs()){
            stepExecutionContext.addDataToStepData(step.getStepFinalName(), data.getAliasName(), data.getFullQualifiedName(),false);
        }
        for(DataDefinitionsDeclaration data: step.getStepDefinition().getOutputs()){
            stepExecutionContext.addDataToStepData(step.getStepFinalName(),data.getAliasName(), data.getFullQualifiedName(),true);
        }

    }

    private static FlowStatus endStep(FlowStatus flowStatus, StepUsageDecleration step, StepStatus stepStatus) {
        if (stepStatus == StepStatus.FAIL) {// if the step failed all the flow his failed and we need to stop.
            if(step.skipIfFail()){
                flowStatus =FlowStatus.WARNING;
            }
            else {
                flowStatus = FlowStatus.FAIL;
            }
        }
        else if (stepStatus == StepStatus.WARNING) {
            flowStatus = FlowStatus.WARNING;
        }
        return flowStatus;
    }

    private static void startStep(StepExecutionContext stepExecutionContext, StepUsageDecleration step) {
        stepExecutionContext.updateCustomMap(step);
        stepExecutionContext.addStepData(step);
        stepExecutionContext.setStartStep(step.getStepFinalName());
    }

    /***
     * Updates the results of the flow run
     * @param currFlow
     * @param stepExecutionContext
     * @param flowStatus
     * @param start
     */
    private static void finishExecution(FlowExecution currFlow, StepExecutionContext stepExecutionContext, FlowStatus flowStatus, Instant start) {
        currFlow.setTotalTime(Duration.between(start, Instant.now()));
        currFlow.setFlowStatus(flowStatus);
        stepExecutionContext.addFormalOutput(currFlow);
        currFlow.setStepsData(stepExecutionContext.getStepsData());
        currFlow.setHasExecuted(true);
        currFlow.setAllData(stepExecutionContext.getAllData());
    }

    private static StepStatus invokeStep(StepExecutionContext stepExecutionContext, StepUsageDecleration step) {
        StepStatus stepStatus = step.getStepDefinition().invoke(stepExecutionContext, step.getNameToAlias(), step.getStepFinalName());
        return stepStatus;
    }

    private static void setStepTotalTime(StepExecutionContext stepExecutionContext, StepUsageDecleration step, Instant stepStart, Instant stepEnd) {
        stepExecutionContext.setTotalTime(step.getStepFinalName(), Duration.between(stepStart, stepEnd));
    }


    private static Instant updateTime(FlowExecution currFlow) {
        Instant start = Instant.now();
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        currFlow.setFormattedStartTime(currentTime.format(formatter));
        return start;
    }
}
