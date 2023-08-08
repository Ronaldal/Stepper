package DTO.FlowDetails;

import DTO.FlowDetails.StepDetails.FlowIODetails.Input;
import DTO.FlowDetails.StepDetails.FlowIODetails.Output;
import DTO.FlowDetails.StepDetails.StepDetails;
import StepperEngine.Flow.api.FlowDefinition;
import StepperEngine.Step.api.DataDefinitionsDeclaration;
import StepperEngine.StepperReader.XMLReadClasses.Continuation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * holds the details of a flow
 */
public class FlowDetails {


    private final String flowName;
    private final int continuationNumber;
    private final String flowDescription;
    private final boolean readOnly;

    private final List<String> formalOutputs;

    private final List<StepDetails> steps = new ArrayList<>();
    private final List<Input> freeInputs = new ArrayList<>();
    private final List<String> continuationNames;
    private final List<Output> outputs = new ArrayList<>();


    public FlowDetails(FlowDefinition flow){
        this.flowName = flow.getName();
        this.flowDescription = flow.getDescription();
        this.readOnly = flow.isReadOnlyFlow();
        buildFreeInputsDetails(flow);
        formalOutputs = buildFormalOutputs(flow);
        buildSteps(flow);
        buildOutputs(flow);
        continuationNumber =flow.getContinuationNumber();
        this.continuationNames = flow.getContinuation().stream()
                .map(Continuation::getTargetFlow)
                .collect(Collectors.toList());
    }

    public List<String> getContinuationNames() {
        return continuationNames;
    }


    public int getContinuationNumber() {
        return continuationNumber;
    }

    /**
     * extract the outputs details from the flow definition,
     * @param flow the flow definition to extract the outputs from
     */
    private void buildOutputs(FlowDefinition flow) {
        for(String outputName: flow.getAllOutputs().keySet()){
            DataDefinitionsDeclaration data = flow.getAllOutputs().get(outputName).getKey();
            outputs.add(new Output(outputName, data.dataDefinition().getName(),
                    flow.getAllOutputs().get(outputName).getValue(),data.getFullQualifiedName()));
        }
    }

    /**
     * extract the steps details from the flow definition,
     * @param flow the flow definition to extract the steps from
     */
    private void buildSteps(FlowDefinition flow) {
        flow.getSteps().forEach(step -> steps.add(new StepDetails(step)));
    }

    /**
     * extract the formal outputs details from the flow definition,
     * @param flow the flow definition to extract the formal outputs from
     */
    private List<String> buildFormalOutputs(FlowDefinition flow) {
        final List<String> formalOutputs;
        formalOutputs = Arrays.asList(flow.outputStrings().split(","));
        return formalOutputs;
    }

    /**
     * extract the inputs details from the flow definition,
     * @param flow the flow definition to extract the inputs from
     */
    private void buildFreeInputsDetails(FlowDefinition flow) {
        for (Map.Entry<DataDefinitionsDeclaration, List<String>> entry : flow.getFreeInputsWithOptional().entrySet()) {
            DataDefinitionsDeclaration data = entry.getKey();
            freeInputs.add(new Input(
                    data.getAliasName(),
                    data.dataDefinition().getName(),
                    String.valueOf(data.necessity()),
                    entry.getValue(),
                    data.userString(),
                    data.getFullQualifiedName()));
        }
        freeInputs.sort(Comparator.comparing(Input::getNecessity));
    }


    public String getFlowName() {
        return flowName;
    }


    public String getFlowDescription() {
        return flowDescription;
    }


    public List<String> getFormalOutputs() {
        return formalOutputs;
    }


    public boolean isFlowReadOnly() {
        return readOnly;
    }


    public String isFlowReadOnlyString() {
        return "The flow is "+ (isFlowReadOnly()? "":"not ") + "read only";
    }

    public List<StepDetails> getSteps() {
        return steps;
    }


    public List<Input> getFreeInputs() {
        return freeInputs;
    }


    public List<Output> getOutputs() {
        return outputs;
    }

    public List<String> getStepsNames() {
      return  steps.stream().map(StepDetails::getStepName).collect(Collectors.toList());
    }
}
