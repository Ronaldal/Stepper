package StepperEngine.DataDefinitions.impl;

import StepperEngine.DataDefinitions.api.DataDefinitionAbstract;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class StepperJSON extends DataDefinitionAbstract {
    public StepperJSON(){
        super("JSON", true, JsonElement.class);
    }
}
