package StepperEngine.DataDefinitions.impl;

import StepperEngine.DataDefinitions.api.DataDefinitionAbstract;

public class StepperFilePath extends DataDefinitionAbstract {
    public StepperFilePath(){
        super("File path", true, String.class);
    }
}
