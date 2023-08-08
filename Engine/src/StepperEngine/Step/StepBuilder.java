package StepperEngine.Step;

import StepperEngine.Step.api.StepDefinition;

public class StepBuilder  {
    /***
     * creates a step by his name with reflection.
     * @param name
     * @return an instance of the step.
     */

    public StepDefinition getStepInstance(String name){
        try {
            Class<?> stepClass = Class.forName(name);
            Object newStep = stepClass.newInstance();
            return (StepDefinition)newStep;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
