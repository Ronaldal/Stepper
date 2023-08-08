package StepperEngine.Flow.execute;

import StepperEngine.Flow.api.FlowDefinition;

public class FlowExecutionWithUser extends FlowExecution{

    private final String userExecuting;
    private final Boolean isManager;


    public FlowExecutionWithUser(FlowDefinition flowDefinition, String username,Boolean isManager) {
        super(flowDefinition);
        this.userExecuting = username;
        this.isManager=isManager;
    }

    public String getUserExecuting() {
        return userExecuting;
    }

    public Boolean getIsManager() {
        return isManager;
    }
}
