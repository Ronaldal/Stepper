package StepperEngine.Step.api;

import StepperEngine.Flow.execute.context.StepExecutionContext;

import java.util.List;
import java.util.Map;

public interface StepDefinition {
    StepStatus invoke(StepExecutionContext context, Map<String, String> nameToAlias, String stepName);
    String getName();
    Boolean isReadOnly();
    void addInput(DataDefinitionsDeclaration newInput);
    void addOutput(DataDefinitionsDeclaration newOutput);

    List<DataDefinitionsDeclaration> getInputs();

    List<DataDefinitionsDeclaration> getOutputs();
}
