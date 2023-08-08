package StepperEngine.StepperReader.XMLReadClasses;


import StepperEngine.Step.StepDefinitionRegistry;
import generated.STStepInFlow;

import java.io.Serializable;

public class StepInFlow implements Serializable {

    private String name;

    private String alias;

    private Boolean continueIfFailing;
    private StepDefinitionRegistry stepDefinitionRegistry;
    private int numOfStep;

    private static Integer count = 0;

    StepInFlow(STStepInFlow stStepInFlow){
        this.name = stStepInFlow.getName();
        this.alias = stStepInFlow.getAlias();
        this.continueIfFailing = stStepInFlow.isContinueIfFailing();
        numOfStep = count++;
    }

    public static void resetIndexing(){
        count = 0;
    }

    public int getNumOfStep() {
        return numOfStep;
    }


    StepInFlow(String name, String alias, Boolean continueIfFailing){
        this.name  = name;
        this.alias = alias;
        this.continueIfFailing = continueIfFailing;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public Boolean isContinueIfFailing() {
        return continueIfFailing;
    }
}
