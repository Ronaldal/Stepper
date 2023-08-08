package StepperEngine.Step.impl;

import StepperEngine.DataDefinitions.impl.DataDefinitionRegistry;
import StepperEngine.Flow.execute.context.StepExecutionContext;
import StepperEngine.Step.api.DataDefinitionDeclarationImpl;
import StepperEngine.Step.api.DataNecessity;
import StepperEngine.Step.api.StepDefinitionAbstract;
import StepperEngine.Step.api.StepStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class SpendSomeTimeStep extends StepDefinitionAbstract {
    public SpendSomeTimeStep(){
        super("Spend Some Time", true);
        this.addInput(new DataDefinitionDeclarationImpl("TIME_TO_SPEND","Total sleeping time (sec)", DataNecessity.MANDATORY, DataDefinitionRegistry.NUMBER));
    }
    /***
     *Sleep operation for a time limited in seconds.
     * @param context-interface that saves all system data
     * @param nameToAlias-Map of the name of the information definition to the name of the information in the current flow
     * @param stepName- The step name in the flow
     */
    @Override
    public StepStatus invoke(StepExecutionContext context, Map<String, String> nameToAlias, String stepName)  {
        Integer timeToSleep= context.getDataValue(nameToAlias.get("TIME_TO_SPEND"), Integer.class);
        Instant start = Instant.now();
        if(timeToSleep == null || timeToSleep <= 0){
            context.setInvokeSummery(stepName,"Cannot sleep if the time is less then 1 !!!");
            context.setStepStatus(stepName,StepStatus.FAIL);
            context.setTotalTime(stepName,Duration.between(start, Instant.now()));
            return StepStatus.FAIL;
        }
        context.addLog(stepName,"About to sleep for "+timeToSleep.toString()+" seconds…");
        try {
            Thread.sleep(timeToSleep*1000);
            context.addLog(stepName,"Done sleeping…");
        } catch (InterruptedException e) {
            context.setInvokeSummery(stepName,e.getMessage());
            context.setStepStatus(stepName,StepStatus.FAIL);
            context.setTotalTime(stepName,Duration.between(start, Instant.now()));
            return StepStatus.FAIL;
        }
        context.setInvokeSummery(stepName,"The step "+stepName+", performed a sleep for "+timeToSleep*1000+
                " milliseconds.");
        context.setStepStatus(stepName,StepStatus.SUCCESS);
        context.setTotalTime(stepName,Duration.between(start, Instant.now()));
        return StepStatus.SUCCESS;
    }
}
