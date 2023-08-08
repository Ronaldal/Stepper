package DTO.FlowDetails.StepDetails;

import DTO.FlowDetails.StepDetails.FlowIODetails.Input;
import DTO.FlowDetails.StepDetails.FlowIODetails.Output;
import StepperEngine.Flow.api.StepUsageDecleration;
import StepperEngine.Step.api.DataDefinitionsDeclaration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * holds a step details from a certain flow.
 */
public class StepDetails {


    private final String stepName; // its final name.
    private final boolean readOnly;

    private List<Input> inputs=new ArrayList<>();
    private List<Output> outputs=new ArrayList<>();

    public StepDetails(StepUsageDecleration step){
        stepName = step.getStepFinalName();
        readOnly = step.isReadOnlyStep();
        createInputsList(step);
        createOutputsList(step);
    }
    private void createOutputsList(StepUsageDecleration step) {
        Pair<String,String> stepRelatedData;
        for(DataDefinitionsDeclaration output : step.getStepDefinition().getOutputs()){
            if (step.getOutputDataMap().get(output.getAliasName())==null){
                outputs.add(new Output(output.getAliasName(), output.dataDefinition().getName(), "not connected", "not connected",output.getFullQualifiedName()));
            }else {
                for (Pair<String, String> pair : step.getOutputDataMap().get(output.getAliasName())) {
                    outputs.add(new Output(output.getAliasName(), output.dataDefinition().getName(), pair.getKey(), pair.getValue(),output.getFullQualifiedName()));
                }
            }

        }
    }
    private void createInputsList(StepUsageDecleration step) {
        Pair<String,String> stepRelatedData;
        for(DataDefinitionsDeclaration input : step.getStepDefinition().getInputs()){
            stepRelatedData= step.getDataMap().containsKey(input.getFullQualifiedName()) ?
                    step.getDataMap().get(input.getFullQualifiedName()) : new Pair<>("Free Input","Free Input");
            if(input.isInitial())
                stepRelatedData=new Pair<>("Initial input","Initial input");
            inputs.add(new Input(input.getAliasName(),input.dataDefinition().getName(), String.valueOf(input.necessity()),
                    stepRelatedData, input.userString(), input.getFullQualifiedName()));
        }
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public String getStepName() {
        return stepName;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}
