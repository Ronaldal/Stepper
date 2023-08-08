package StepperEngine.StepperReader.XMLReadClasses;

import generated.STCustomMapping;

import java.io.Serializable;

public class CustomMapping implements Serializable {


    private String targetStep;

    private String targetData;

    private String sourceStep;

    private String sourceData;

    public CustomMapping(STCustomMapping stCustomMapping){
        this.sourceData = stCustomMapping.getSourceData();
        this.sourceStep = stCustomMapping.getSourceStep();
        this.targetData = stCustomMapping.getTargetData();
        this.targetStep = stCustomMapping.getTargetStep();
    }

    public CustomMapping(String targetStep, String targetData, String sourceStep, String sourceData) {
        this.targetStep = targetStep;
        this.targetData = targetData;
        this.sourceStep = sourceStep;
        this.sourceData = sourceData;
    }

    public String getTargetStep() {
        return targetStep;
    }

    public void setTargetStep(String targetStep) {
        this.targetStep = targetStep;
    }

    public String getTargetData() {
        return targetData;
    }

    public void setTargetData(String targetData) {
        this.targetData = targetData;
    }

    public String getSourceStep() {
        return sourceStep;
    }

    public void setSourceStep(String sourceStep) {
        this.sourceStep = sourceStep;
    }

    public String getSourceData() {
        return sourceData;
    }

    public void setSourceData(String sourceData) {
        this.sourceData = sourceData;
    }
}
