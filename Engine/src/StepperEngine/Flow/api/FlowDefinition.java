package StepperEngine.Flow.api;

import StepperEngine.Flow.FlowBuildExceptions.FlowBuildException;
import StepperEngine.Step.api.DataDefinitionsDeclaration;
import StepperEngine.StepperReader.XMLReadClasses.Continuation;
import StepperEngine.StepperReader.XMLReadClasses.ContinuationMapping;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FlowDefinition {
    Map<String, DataDefinitionsDeclaration> getAllDataDefinitions();

    boolean hasContinuation();
    List<Continuation> getContinuation();
    String getName();
    String getDescription();
    int getContinuationNumber();
    List<StepUsageDecleration> getSteps();
    String outputStrings();
    List<String> isFlowValid();

    void customMapping();
    void autoMapping();

   boolean isReadOnlyFlow();
    Map<DataDefinitionsDeclaration, List<String>> getFreeInputsWithOptional();
    Map<String , Pair<DataDefinitionsDeclaration,String>> getAllOutputs();
    Map<String, Pair<DataDefinitionsDeclaration, String>> getFormalOuputs();

    Set<DataDefinitionsDeclaration> getFreeInputs();
    void isDDExist(ContinuationMapping continuationMapping) throws FlowBuildException;
    void isInputExist(ContinuationMapping continuationMapping)throws FlowBuildException;
   Map<String, Pair<DataDefinitionsDeclaration, Object>> getInitialInputs();

    DataDefinitionsDeclaration getFreeInputByName(String sourceData);
    void addContinuationMapping(String source,String target);
    Map<String,String> getContinuationMapping();

    DataDefinitionsDeclaration getDDByName(String data);

    List<String> getFreeInputStepsRelated(String dataName);

}
