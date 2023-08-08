package StepperEngine.Step.impl;

import StepperEngine.DataDefinitions.impl.DataDefinitionRegistry;
import StepperEngine.Flow.execute.context.StepExecutionContext;
import StepperEngine.Step.api.DataDefinitionDeclarationImpl;
import StepperEngine.Step.api.DataNecessity;
import StepperEngine.Step.api.StepDefinitionAbstract;
import StepperEngine.Step.api.StepStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class FileDumper extends StepDefinitionAbstract {
    public FileDumper() {
        super("File Dumper", true);
        addInput(new DataDefinitionDeclarationImpl("CONTENT", "Content", DataNecessity.MANDATORY, DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("FILE_NAME", "Target file path", DataNecessity.MANDATORY, DataDefinitionRegistry.FILE_PATH));
        addOutput(new DataDefinitionDeclarationImpl("RESULT", "File Creation Result", DataNecessity.NA, DataDefinitionRegistry.STRING));
    }

    /***
     * Generates a text file by name and name in it's content
     * @param context-interface that saves all system data
     * @param nameToAlias-Map of the name of the information definition to the name of the information in the current flow
     * @param stepName- The step name in the flow
     */
    @Override
    public StepStatus invoke(StepExecutionContext context, Map<String, String> nameToAlias, String stepName)  {
        Instant start = Instant.now();
        String content = context.getDataValue(nameToAlias.get("CONTENT"), String.class);
        String fileName = context.getDataValue(nameToAlias.get("FILE_NAME"), String.class);
        context.addLog(stepName,"About to create file named " + fileName);
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(content);
            writer.close();
            context.storeValue(nameToAlias.get("RESULT"),"SUCCESS");
            if(content.length()==0) {
                context.addLog(stepName,"The content is empty");
                context.setInvokeSummery(stepName,"The file "+fileName+"create, but the content was empty. ");
                context.setStepStatus(stepName,StepStatus.WARNING);
            }else {
                context.setInvokeSummery(stepName,"The file "+fileName+"created successfully.");
                context.setStepStatus(stepName, StepStatus.SUCCESS);
            }
        } catch (IOException e) {
            context.setInvokeSummery(stepName,"The file "+fileName+" already exists.");
            context.storeValue(nameToAlias.get("RESULT"),"FAIL,"+e.toString());
            context.setStepStatus(stepName,StepStatus.FAIL);
        }
        context.setTotalTime(stepName, Duration.between(start, Instant.now()));
        return context.getStepStatus(stepName);
    }
}


