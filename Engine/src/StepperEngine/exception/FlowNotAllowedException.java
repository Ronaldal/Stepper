package StepperEngine.exception;

public class FlowNotAllowedException extends RuntimeException{
    private final String flowName;


    public FlowNotAllowedException(String flowName) {
        this.flowName = flowName;

    }
    public String getFlowName() {
        return flowName;
    }

}
