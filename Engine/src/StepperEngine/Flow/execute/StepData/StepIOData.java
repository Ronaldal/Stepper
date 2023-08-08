package StepperEngine.Flow.execute.StepData;

public class StepIOData {
    private final String finalName;
    private final String dataDef;
    private final Object content;
    private final String necessity;

    private final boolean isOutput;

    public StepIOData(boolean isOutput, String finalName, String dataDef, Object content, String necessity) {
        this.isOutput = isOutput;
        this.finalName = finalName;
        this.dataDef = dataDef;
        this.content = content;
        this.necessity = necessity;
    }


}
