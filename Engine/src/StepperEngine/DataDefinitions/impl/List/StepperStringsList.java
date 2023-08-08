package StepperEngine.DataDefinitions.impl.List;

import StepperEngine.DataDefinitions.List.StringListDataDef;
import StepperEngine.DataDefinitions.api.DataDefinitionAbstract;

public class StepperStringsList extends DataDefinitionAbstract {
    public StepperStringsList(){
        super("List", false, StringListDataDef.class);
    }
}
