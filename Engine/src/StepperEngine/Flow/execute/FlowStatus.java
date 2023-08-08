package StepperEngine.Flow.execute;

public enum FlowStatus {
    SUCCESS,
    FAIL,
    WARNING,
    DONE;

    public static String getAsString(FlowStatus flowStatus){
        switch (flowStatus){
            case WARNING:return "WARNING";
            case FAIL:return "FAIL";
            case SUCCESS:return "SUCCESS";
            case DONE:return "DONE";
            default:return null;
        }
    }
}
