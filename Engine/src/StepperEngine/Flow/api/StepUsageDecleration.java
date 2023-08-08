package StepperEngine.Flow.api;

import StepperEngine.Step.api.StepDefinition;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public interface StepUsageDecleration {

    StepDefinition getStepDefinition();
    String getStepFinalName();
    void setAliasName(String alias);
    boolean skipIfFail();

    Integer getIndex();

    //void addInputToMap(String dataName, String stepRefName, String dataNameInStepRef);
    void addInputToMap(String dataName, StepUsageDecleration stepRefName, String dataNameInStepRef);
    void addOutputToMap(String dataName, String stepRefName, String dataNameInStepRef);
    Map<String, List<Pair<String, String>>> getOutputDataMap();

    Map<String, Pair<String, String>> getDataMap();

    Pair<String, String> getInputRef(String input);
    Map<String, String> getNameToAlias();
    boolean isReadOnlyStep();
    String getQName(String dataName);

}
