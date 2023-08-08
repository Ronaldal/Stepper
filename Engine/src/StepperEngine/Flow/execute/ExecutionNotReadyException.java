package StepperEngine.Flow.execute;

public class ExecutionNotReadyException extends Exception {

    private final String executionName;
    public ExecutionNotReadyException(String message, String flowName) {
        super(message);
        this.executionName = flowName;
    }


    @Override
    public String toString(){
        return getMessage();
    }

    @Override
    public String getMessage(){
        return "execution "+executionName+ " execution error\n"+super.getMessage();
    }
}
