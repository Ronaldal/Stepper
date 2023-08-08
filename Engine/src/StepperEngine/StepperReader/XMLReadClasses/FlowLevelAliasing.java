package StepperEngine.StepperReader.XMLReadClasses;

import generated.STFlowLevelAliasing;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class FlowLevelAliasing implements Serializable {
    private List<FlowLevelAlias> flowLevelAliases;

    public FlowLevelAliasing(STFlowLevelAliasing stFlowLevelAliasing){
        if(stFlowLevelAliasing != null) {
            flowLevelAliases = stFlowLevelAliasing.getSTFlowLevelAlias().stream()
                    .map(element -> new FlowLevelAlias(element))
                    .collect(Collectors.toList());
        }
    }
    public void addFlowLevelAlias(FlowLevelAlias flowLevelAlias){
        flowLevelAliases.add(flowLevelAlias);
    }

    public List<FlowLevelAlias> getFlowLevelAliases() {
        return flowLevelAliases;
    }
}
