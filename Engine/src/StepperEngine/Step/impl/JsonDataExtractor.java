package StepperEngine.Step.impl;

import StepperEngine.DataDefinitions.impl.DataDefinitionRegistry;
import StepperEngine.Flow.execute.context.StepExecutionContext;
import StepperEngine.Step.api.DataDefinitionDeclarationImpl;
import StepperEngine.Step.api.DataNecessity;
import StepperEngine.Step.api.StepDefinitionAbstract;
import StepperEngine.Step.api.StepStatus;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class JsonDataExtractor extends StepDefinitionAbstract {
    public JsonDataExtractor() {
        super("Json Data Extractor", true);
        this.addInput(new DataDefinitionDeclarationImpl("JSON","Json source", DataNecessity.MANDATORY, DataDefinitionRegistry.JSON));
        this.addInput(new DataDefinitionDeclarationImpl("JSON_PATH","Data", DataNecessity.MANDATORY, DataDefinitionRegistry.STRING));
        this.addOutput(new DataDefinitionDeclarationImpl("VALUE","Data value",DataNecessity.NA,DataDefinitionRegistry.STRING));
    }

    @Override
    public StepStatus invoke(StepExecutionContext context, Map<String, String> nameToAlias, String stepName) {
        Instant start = Instant.now();
        Gson gson=new Gson();
        String invokeSummery,value="";
        JsonElement jsonElement = context.getDataValue(nameToAlias.get("JSON"), JsonElement.class);
        String jsonPath=context.getDataValue(nameToAlias.get("JSON_PATH"),String.class);
        String[] jsonPaths=jsonPath.split("\\|");
        if(jsonElement==null){
            context.setStepStatus(stepName,StepStatus.FAIL);
            invokeSummery="The json object dosent exsits";
        } else {
            for (int i = 0; i < jsonPaths.length; i++) {
                try {
                    System.out.println(jsonPaths[i]);
                    String extractedData = JsonPath.read(jsonElement.toString(), jsonPaths[i]).toString();
                    if (extractedData == "") {
                        context.addLog(stepName, "No value found for json path " + jsonPaths[i]);
                    } else {
                        context.addLog(stepName, "Extracting data " + jsonPaths[i] + ". Value:  " + extractedData);
                        if (i + 1 != jsonPaths.length)
                            extractedData += ",";
                    }
                    value += extractedData;
                } catch (PathNotFoundException e) {
                    context.addLog(stepName, "No value found for json path " + jsonPaths[i]);
                }
            }
            if (jsonPath == null) {
                context.setStepStatus(stepName, StepStatus.WARNING);
                invokeSummery="There are no json path's";
            } else {
                context.setStepStatus(stepName, StepStatus.SUCCESS);
                invokeSummery="Reading data from json done successfully";
            }
        }


        context.setInvokeSummery(stepName, invokeSummery);
        context.storeValue(nameToAlias.get("VALUE"),value);
        context.setTotalTime(stepName, Duration.between(start, Instant.now()));
        return context.getStepStatus(stepName);
    }
}
