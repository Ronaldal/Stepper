package StepperEngine.StepperReader.XMLReadClasses;



import generated.STStepper;

import java.io.Serializable;

public class TheStepper implements Serializable {

    private Flows flows;
    private int threadPool;


    public TheStepper(STStepper stStepper){
        this.flows = new Flows(stStepper.getSTFlows());
        threadPool=stStepper.getSTThreadPool();
    }
    public Flows getFlows() {
        return flows;
    }

    public void setFlows(Flows flows) {
        this.flows = flows;
    }

    public int getThreadPool() {
        return threadPool;
    }
}
