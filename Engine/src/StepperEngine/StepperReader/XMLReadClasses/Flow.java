package StepperEngine.StepperReader.XMLReadClasses;

import generated.*;

import java.io.Serializable;
import java.util.*;

public class Flow implements Serializable {
    private String flowOutput;
    private CustomMappings customMappings;
    private FlowLevelAliasing flowLevelAliasing;
    private List<Continuation> continuations=new ArrayList<>();
    private List<InitialInputValue> initialInputValues=new ArrayList<>();

    private StepsInFlow stepsInFlow;

    private String flowDescription;

    private String name;

    public Flow(STFlow stflow){
        this.flowOutput = stflow.getSTFlowOutput();
        this.flowDescription = stflow.getSTFlowDescription();
        this.stepsInFlow = new StepsInFlow(stflow.getSTStepsInFlow());
        this.name = stflow.getName();
        this.customMappings = new CustomMappings(stflow.getSTCustomMappings());
        this.flowLevelAliasing = new FlowLevelAliasing(stflow.getSTFlowLevelAliasing());
        Optional.ofNullable(stflow.getSTInitialInputValues())
                .map(STInitialInputValues::getSTInitialInputValue)
                .orElse(Collections.emptyList())
                .stream()
                .map(InitialInputValue::new)
                .forEach(initialInputValues::add);
        Optional.ofNullable(stflow.getSTContinuations())
                .map(STContinuations::getSTContinuation)
                .orElse(Collections.emptyList())
                .stream()
                .map(Continuation::new)
                .forEach(continuations::add);
    }



    public String getFlowOutput() {
        return flowOutput;
    }

    public CustomMappings getCustomMappings() {
        return customMappings;
    }

    public FlowLevelAliasing getFlowLevelAliasing() {
        return flowLevelAliasing;
    }

    public StepsInFlow getStepsInFlow() {
        return stepsInFlow;
    }

    public String getFlowDescription() {
        return flowDescription;
    }

    public String getName() {
        return name;
    }

    public List<Continuation> getContinuations() {
        return continuations;
    }

    public List<InitialInputValue> getInitialInputValues() {
        return initialInputValues;
    }



    public Flow(String flowOutput, CustomMappings customMappings, FlowLevelAliasing flowLevelAliasing, StepsInFlow stepsInFlow, String flowDescription, String name) {
        this.flowOutput = flowOutput;
        this.customMappings = customMappings;
        this.flowLevelAliasing = flowLevelAliasing;
        this.stepsInFlow = stepsInFlow;
        this.flowDescription = flowDescription;
        this.name = name;
    }
}
