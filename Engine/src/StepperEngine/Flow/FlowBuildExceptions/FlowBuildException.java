package StepperEngine.Flow.FlowBuildExceptions;

public class FlowBuildException extends Exception {

    private final String flowName;


    public FlowBuildException(String message, String flowName){

        super(message);
        this.flowName = flowName;
    }

    public String getFlowName() {
        return flowName;
    }

    @Override
    public String toString(){
        return getMessage();
    }

    @Override
    public String getMessage(){
        return "Error while building the flow "+flowName+ "\n"+super.getMessage();
    }


}
