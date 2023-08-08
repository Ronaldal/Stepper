package StepperEngine.DataDefinitions.impl;

import StepperEngine.DataDefinitions.Enumeration.MethodEnumerator;
import StepperEngine.DataDefinitions.api.DataDefinitionAbstract;

public class StepperMethodEnumerator extends DataDefinitionAbstract {
    StepperMethodEnumerator(){
        super("Enumerator",true, MethodEnumerator.class);
    }
}
