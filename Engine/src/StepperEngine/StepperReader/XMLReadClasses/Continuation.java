package StepperEngine.StepperReader.XMLReadClasses;

import generated.STContinuation;
import generated.STContinuationMapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Continuation implements Serializable {
    private String targetFlow;
    private List<ContinuationMapping> continuationMappings=new ArrayList<>();
    public Continuation(STContinuation stContinuation)
    {
        targetFlow=stContinuation.getTargetFlow();
        for(STContinuationMapping stContinuationMapping:stContinuation.getSTContinuationMapping()){
            continuationMappings.add(new ContinuationMapping(stContinuationMapping));
        }
    }

    public String getTargetFlow() {
        return targetFlow;
    }

    public List<ContinuationMapping> getContinuationMappings() {
        return continuationMappings;
    }
}
