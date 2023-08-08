package StepperEngine.DataDefinitions.impl;

import StepperEngine.DataDefinitions.api.DataDefinitionAbstract;

public class StepperNumber extends DataDefinitionAbstract {
    public StepperNumber(){
        super("Number", true, Integer.class);
    }
}
