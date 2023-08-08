package StepperEngine.DataDefinitions.impl.Mapping;

import StepperEngine.DataDefinitions.Mapping.NumberMapping;
import StepperEngine.DataDefinitions.api.DataDefinitionAbstract;

public class StepperNumberMapping extends DataDefinitionAbstract {
    public StepperNumberMapping() {
        super("Mapping", false, NumberMapping.class);
    }
}
