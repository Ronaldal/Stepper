package StepperEngine.DataDefinitions.impl;

import StepperEngine.DataDefinitions.Enumeration.ProtocolEnumerator;
import StepperEngine.DataDefinitions.api.DataDefinitionAbstract;

public class StepperProtocolEnumerator extends DataDefinitionAbstract {
    StepperProtocolEnumerator(){
        super("Enumerator",true, ProtocolEnumerator.class);
    }
}
