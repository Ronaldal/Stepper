package StepperEngine.DataDefinitions.impl.Relation;

import StepperEngine.DataDefinitions.Relation.RelationOfStringRows;
import StepperEngine.DataDefinitions.api.DataDefinitionAbstract;

public class StepperRelationString extends DataDefinitionAbstract {
    public StepperRelationString(){
        super("Relation", false, RelationOfStringRows.class);
    }


}
