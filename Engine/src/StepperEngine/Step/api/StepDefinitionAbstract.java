package StepperEngine.Step.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * The base class of the various steps
 */
public abstract class StepDefinitionAbstract implements StepDefinition, Serializable {
    private final String name;
    private final boolean isReadOnly;

    private final List<DataDefinitionsDeclaration> inputs = new ArrayList<>();
    private final List<DataDefinitionsDeclaration> outputs = new ArrayList<>();

    protected StepDefinitionAbstract(String name, boolean isReadOnly){
        this.name = name;
        this.isReadOnly = isReadOnly;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    public List<DataDefinitionsDeclaration> getInputs() {
        return inputs;
    }

    @Override
    public List<DataDefinitionsDeclaration> getOutputs() {
        return outputs;
    }

    @Override
    public void addInput(DataDefinitionsDeclaration newInput){
        inputs.add(newInput);
    }

    @Override
    public void addOutput(DataDefinitionsDeclaration newOutput)
    {
        outputs.add(newOutput);
    }
}
