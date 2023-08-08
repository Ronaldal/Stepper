package StepperEngine.StepperReader.XMLReadClasses;

import generated.STFlowLevelAlias;

import java.io.Serializable;

public class FlowLevelAlias implements Serializable {

    private String step;

    private String sourceDataName;

    private  String alias;

    public String getStep() {
        return step;
    }

    public String getSourceDataName() {
        return sourceDataName;
    }

    public String getAlias() {
        return alias;
    }

    public FlowLevelAlias(STFlowLevelAlias stFlowLevelAlias){
        this.step = stFlowLevelAlias.getStep();
        this.alias = stFlowLevelAlias.getAlias();
        this.sourceDataName = stFlowLevelAlias.getSourceDataName();
    }

    public FlowLevelAlias(String step, String sourceDataName, String alias) {
        this.step = step;
        this.sourceDataName = sourceDataName;
        this.alias = alias;
    }
}
