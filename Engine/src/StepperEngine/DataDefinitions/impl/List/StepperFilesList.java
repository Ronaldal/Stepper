package StepperEngine.DataDefinitions.impl.List;

import StepperEngine.DataDefinitions.List.FilesListDataDef;
import StepperEngine.DataDefinitions.api.DataDefinitionAbstract;

public class StepperFilesList extends DataDefinitionAbstract {
    public StepperFilesList(){
        super("List", false, FilesListDataDef.class);
    }
}
