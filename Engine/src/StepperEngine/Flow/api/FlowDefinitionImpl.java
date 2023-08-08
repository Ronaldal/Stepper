package StepperEngine.Flow.api;

import StepperEngine.DataDefinitions.Enumeration.MethodEnumerator;
import StepperEngine.DataDefinitions.Enumeration.ProtocolEnumerator;
import StepperEngine.DataDefinitions.Enumeration.ZipEnumerator;
import StepperEngine.Flow.FlowBuildExceptions.FlowBuildException;
import StepperEngine.StepperReader.XMLReadClasses.*;
import StepperEngine.Step.StepBuilder;
import StepperEngine.Step.StepDefinitionRegistry;
import StepperEngine.Step.api.DataDefinitionsDeclaration;
import StepperEngine.Step.api.DataNecessity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FlowDefinitionImpl implements FlowDefinition, Serializable {

    private final Flow flow;

    private Boolean valid;
    private Boolean hasContinuation=true;

    private Boolean isReadOnly = true;

    //   <output name, <data definition, the step that related to him>
    private Map<String , Pair<DataDefinitionsDeclaration,String>> allOuputs=new HashMap<>();
    private Map<String , Pair<DataDefinitionsDeclaration,String>> formalOuputs =new HashMap<>();
    private Map<String,Pair<DataDefinitionsDeclaration,Object>> initialInputs=new HashMap<>();
    private Map<String,DataDefinitionsDeclaration> allDataDefinitions=new HashMap<>();
    private Set<DataDefinitionsDeclaration> freeInputs;
    private Set<DataDefinitionsDeclaration> allInputs;
    private Map<DataDefinitionsDeclaration, List<String>> dataToRelatedSteps =new HashMap<>();

    private final List<StepUsageDecleration> steps = new ArrayList<>();

    private final List<String> problems = new ArrayList<>();
    private Map<String,String> continuationMapping=new HashMap<>();

    public FlowDefinitionImpl(Flow flow) throws FlowBuildException {
        freeInputs = new HashSet<>();
        this.flow = flow;
        isFlowValid();
        if(problems.size() > 0){
            valid = false;
            throw new FlowBuildException(problems.get(0), flow.getName());
        }
        valid = true;
        determinateIfFlowIsReadOnly();
        if (flow.getContinuations().size()==0)
            hasContinuation= false;
   }

    public Map<DataDefinitionsDeclaration, List<String>> getDataToRelatedSteps() {
        return dataToRelatedSteps;
    }

    @Override
    public List<String> getFreeInputStepsRelated(String dataName) {
        Optional<DataDefinitionsDeclaration> data = dataToRelatedSteps.keySet().stream()
                .filter(dataDefinitionsDeclaration -> dataDefinitionsDeclaration.getAliasName().equals(dataName))
                .findFirst();
        return data.map(dataDefinitionsDeclaration -> dataToRelatedSteps.get(dataDefinitionsDeclaration)).orElse(null);
    }

    @Override
    public List<Continuation> getContinuation() {
        return flow.getContinuations();
    }

    @Override
    public void isDDExist(ContinuationMapping continuationMapping)throws FlowBuildException {
        if (!allDataDefinitions.containsKey(continuationMapping.getSourceData()))
            throw new FlowBuildException("The data " + continuationMapping.getSourceData() + " doesn't exist",
                    this.getName());

    }


    @Override
    public void isInputExist(ContinuationMapping continuationMapping) throws FlowBuildException {
        if (freeInputs.stream()
                .noneMatch(dd -> continuationMapping.getTargetData().equals(dd.getAliasName())))
            throw new FlowBuildException("The output " + continuationMapping.getTargetData() + " doesn't exist",
                    this.getName());

    }

    private void determinateIfFlowIsReadOnly() {
        for(StepUsageDecleration step:steps){
            if(!step.isReadOnlyStep()) {
                isReadOnly = false;
                break;
            }
        }
        //isReadOnly = steps.stream().anyMatch(step->step.getStepDefinition().isReadOnly());
    }

    public boolean isReadOnlyFlow(){
        return isReadOnly;
    }


    @Override
    public String getName() {
        return flow.getName();
    }

    @Override
    public String getDescription() {
        return flow.getFlowDescription();
    }


    @Override
    public List<StepUsageDecleration> getSteps() {
        return steps;
    }


    @Override
    public String outputStrings() {
        return flow.getFlowOutput();
    }

    /**
     * method to validate the flow.
     * @return a list of problems in string format. if there are no problems, returns an empty list.
     */
    @Override
    public List<String> isFlowValid() {

        if(valid != null) // in case the validation already happened. else, validate the flow.
            return problems;

        buildStepUsages();
        if(!problems.isEmpty()){
            return problems;
        }
        stepAlising();

        flowLevelAliasing();
        if(!problems.isEmpty()){
            return problems;
        }

        customMapping();
        if(!problems.isEmpty()){
            return problems;
        }

        autoMapping();

        initialInputs();
        if(!problems.isEmpty()){
            return problems;
        }

        setFreeInputs();
        if(!problems.isEmpty()){
            return problems;
        }

        checkForDuplicateOutputsNames();
        if(!problems.isEmpty()){
            return problems;
        }
        if(!flow.getFlowOutput().isEmpty()) {
            updateAlloutputs();
        }
        if(!problems.isEmpty()){
            return problems;
        }
        updateAllDataDefinition();
        valid = true;
        return problems;
    }


    /***
     * adding and checking the initial values
     */
    private void initialInputs() {
        setAllInputs();
        boolean isInitialInputExist=false;
        for(InitialInputValue initialInputValue:flow.getInitialInputValues()){
        {
            for (DataDefinitionsDeclaration dd:allInputs)
                if(dd.getAliasName().equals(initialInputValue.getInputName())){
                    isInitialInputExist=true;
                    if(dd.dataDefinition().isUserFriendly())
                        addInitialInput(dd, initialInputValue);
                    else
                        problems.add("You can't add initial input for :" +dd.getAliasName()+"!");
                    break;
                }
            }
            if (!isInitialInputExist)
                problems.add("The input "+ initialInputValue.getInputName()+" not exist!");
            if (!problems.isEmpty())
                return;
        }

    }

    private void addInitialInput(DataDefinitionsDeclaration dd, InitialInputValue initialInputValue) {
        Object value;
        try {
            if (dd.dataDefinition().getType().isAssignableFrom(Integer.class)) {
                value = Integer.parseInt(initialInputValue.getInitialValue());
            } else if (dd.dataDefinition().getType().isAssignableFrom(Double.class)) {
                value = Double.parseDouble(initialInputValue.getInitialValue());
            } else if (dd.dataDefinition().getType().isAssignableFrom(ZipEnumerator.class)) {
                value = ZipEnumerator.valueOf(initialInputValue.getInitialValue().toUpperCase());
            } else if (dd.dataDefinition().getType().isAssignableFrom(ProtocolEnumerator.class)) {
                value=ProtocolEnumerator.valueOf(initialInputValue.getInitialValue().toUpperCase());
            }else if (dd.dataDefinition().getType().isAssignableFrom(MethodEnumerator.class)) {
                value=MethodEnumerator.valueOf(initialInputValue.getInitialValue().toUpperCase());
            } else if (dd.dataDefinition().getType().isAssignableFrom(JsonElement.class)) {
                Gson gson=new Gson();
                value=gson.fromJson(initialInputValue.getInitialValue(), JsonElement.class);
            } else {
                value = initialInputValue.getInitialValue();
            }
            initialInputs.put(initialInputValue.getInputName(),new Pair<>(dd,value));
            steps.forEach(step -> step.getStepDefinition().getInputs().stream()
                    .filter(dd::equals)
                    .forEach(dataDefinition -> dataDefinition.setInitial(true)));
        }
        catch (NumberFormatException n)
        {
            problems.add("The initial input for "+ initialInputValue.getInputName()+ " , not the valid type!"
            +"\n need to enter "+ dd.dataDefinition().getType().getSimpleName());
        }
        catch (IllegalArgumentException e){
            problems.add("The initial input for "+ initialInputValue.getInputName()+ " , not the valid type!");
        }catch (JsonSyntaxException e){
            problems.add("The initial input for "+ initialInputValue.getInputName()+ " , not the valid json type!");
        }
    }


    @Override
    public void autoMapping() {
        for(StepUsageDecleration step:steps)
            autoMap(step);
    }

    /**
     * applying custom mapping on each custom map instance in flow.
     */
    @Override
    public void customMapping() {
        if (flow.getCustomMappings().getCustomMappings() != null) {
            flow.getCustomMappings().getCustomMappings()
                    .forEach(this::customMap);

        }
    }

    @Override
    public int getContinuationNumber() {
        return flow.getContinuations().size();
    }

    /**
     * checks if there are duplicates in the names of the flow outputs.
     */
    private void checkForDuplicateOutputsNames() {
        Set<String> tempSet = new HashSet<>();
        for(StepUsageDecleration step : steps){
            for(DataDefinitionsDeclaration dd: step.getStepDefinition().getOutputs()){
                if(tempSet.contains(dd.getAliasName())){
                    problems.add("Duplicate outputs names \"" + dd.getAliasName() + "\". cannot define flow.");
                    break;
                }
                else {
                    tempSet.add(dd.getAliasName());
                }
            }
        }
    }

    /**
     * finds all free inputs of the flow
     * if a mandatory input that is not a User-friendly one is found, adds it to the problems list.
     */
    private void setFreeInputs() {
        freeInputs = new HashSet<>(steps.stream()
                .flatMap(step -> getStepFreeInputs(step).stream())
                .collect(Collectors.toMap(DataDefinitionsDeclaration::getAliasName,
                        Function.identity(), (a, b) -> a))
                .values());

        for(DataDefinitionsDeclaration dd: freeInputs){
            if(!dd.dataDefinition().isUserFriendly() && dd.necessity() == DataNecessity.MANDATORY && !dd.isInitial()){
                problems.add("Mandatory input \""+ dd.userString() + "\" is not accessible");
            }
        }
        setAllFreeInputs();
    }

    private void setAllInputs() {
        allInputs=steps.stream()
                .flatMap(step -> step.getStepDefinition().getInputs().stream())
                .collect(Collectors.toSet());
    }

    private void setAllFreeInputs(){
        for(DataDefinitionsDeclaration dd : freeInputs){
            for(StepUsageDecleration step: steps){
                if(getStepFreeInputs(step).contains(dd)){
                    dataToRelatedSteps.computeIfAbsent(dd, key -> new ArrayList<>()).add(step.getStepFinalName());
                }
            }
        }
    }


    /**
     * applying step aliasing for each step, if needed.
     */
    public void stepAlising() {
        steps.stream()
                .collect(Collectors.groupingBy(StepUsageDecleration::getStepFinalName))
                .forEach((name, list) -> {
                    if(list.size() > 1)
                        for (int i = 0; i < list.size(); i++)
                            list.get(i).setAliasName(name + "(" + i + ")");

                });
    }


    /**
     * applying flow level aliasing.
     */
    public void flowLevelAliasing(){
        if(flow.getFlowLevelAliasing().getFlowLevelAliases() != null) {
            flow.getFlowLevelAliasing().getFlowLevelAliases()
                    .forEach(this::alias);
        }
    }


    /**
     * taking a FlowLevelAlias instance and updates the source data with his new name.
     * if the source or the step not found, adds to the problems list with the details.
     * @param fla - Flow Level Alias that represents which data should get the alias name.
     */
    private void alias(FlowLevelAlias fla) {
        StepUsageDecleration step = findStepUsageByName(fla.getStep());
        if (step == null) {
            problems.add("Trying to alias step " + fla.getStep() + ", step not found");
            return;
        }

        // check in the output datadef list.
        boolean found = setAliasIfPresent(step.getStepDefinition().getOutputs(), fla.getSourceDataName(), fla.getAlias());
        if (!found) { // if not found in outputs, check in the inputs
            found = setAliasIfPresent(step.getStepDefinition().getInputs(), fla.getSourceDataName(), fla.getAlias());
        }

        //if not found in inputs nor outputs, adds the details to the problems the source data name
        if (!found) {
            problems.add("Source data name not found: " + fla.getSourceDataName());
        }
    }

    /**
     *
     * @param dataDefs - a list of data definition declarations
     * @param sourceDataName - the source data name
     * @param alias - the name we want to give to source data name
     * @return - true if the aliasing succeed, else false.
     */
    private boolean setAliasIfPresent(List<DataDefinitionsDeclaration> dataDefs, String sourceDataName, String alias) {
        return dataDefs.stream()
                .filter(dd -> dd.getName().equals(sourceDataName))
                .findFirst()
                .map(dd -> {
                    dd.setAliasName(alias);
                    return true;
                })
                .orElse(false);
    }


    /**
     * takes the custom mapping instance, and applying on flow.
     * adds to the matching target step's map of inputs, the source step's name and the data as it calls in the
     * source.
     * if source or target not found,
     * @param customMapping a structure for source/target data and step
     */
    private void customMap(CustomMapping customMapping){
        StepUsageDecleration source = findStepUsageByName(customMapping.getSourceStep());
        StepUsageDecleration target = findStepUsageByName(customMapping.getTargetStep());
        if(source == null){
            problems.add("source not found " + customMapping.getSourceStep());
        } else if (target == null) {
            problems.add("target not found " + customMapping.getTargetStep());
        } else if (!isSourceBeforeTarget(source, target)) {
            problems.add("source step " + customMapping.getSourceStep() + "is after target step " + customMapping.getTargetStep());
        }
        else if(!isDataPartOfStepOutPuts(customMapping, source)){
            problems.add("cant find data in source step " + source.getStepFinalName() + ", data name " + customMapping.getSourceData());
        }
        else if(!isDataPartOfStepInPuts(customMapping, target)){
            problems.add("cant find data in target step " + target.getStepFinalName() + ", data name " + customMapping.getTargetData());
        } else if (isInitialInput(customMapping.getTargetData())){
            problems.add("cant create custom map to " + customMapping.getTargetData() + ", it is initial input");
        } else{
            //target.addInputToMap(customMapping.getTargetData(), customMapping.getSourceStep(), customMapping.getSourceData());
            target.addInputToMap(customMapping.getTargetData(), source, customMapping.getSourceData());
            source.addOutputToMap(customMapping.getSourceData(),target.getStepFinalName(),customMapping.getTargetData());
        }
    }


    private boolean isInitialInput(String input) {
       return flow.getInitialInputValues().stream()
                .anyMatch(initialInputValue -> initialInputValue.getInputName().equals(input));
    }


    /**
     * checks if source step is before target step
     * @param source - a stepUsage
     * @param target - a stepUsage
     * @return true if source before target.
     */
    private static boolean isSourceBeforeTarget(StepUsageDecleration source, StepUsageDecleration target) {
        return source.getIndex() < target.getIndex();
    }

    /**
     * checks if source step outputs contains any data with the name specified at the custom mapping instance.
     * @param customMapping - a CustomMapping instance.
     * @param source - a stepUsage
     * @return true if customMapping.sourceData is a data in source.
     */
    private static boolean isDataPartOfStepOutPuts(CustomMapping customMapping, StepUsageDecleration source) {
        return source.getStepDefinition().getOutputs().stream()
                .anyMatch(dd -> dd.getAliasName().equals(customMapping.getSourceData()));
    }

    /**
     * checks if target step inputs contains any data with the name specified at the custom mapping instance.
     * @param customMapping - a CustomMapping instance.
     * @param target - a stepUsage
     * @return true if customMapping.targetData is a data in target.
     */
    private static boolean isDataPartOfStepInPuts(CustomMapping customMapping, StepUsageDecleration target) {
        return target.getStepDefinition().getInputs().stream()
                .anyMatch(dd -> dd.getAliasName().equals(customMapping.getTargetData()));
    }
    /**
     *
     * @param name - a name of a step in the flow
     * @return the step usage of it
     */
    private StepUsageDecleration findStepUsageByName(String name) {
        return steps.stream()
                .filter(step -> step.getStepFinalName().equals(name))
                .findFirst()
                .orElse(null);
    }


    @Override
    public boolean hasContinuation() {
        return hasContinuation;
    }

    private void autoMap(StepUsageDecleration step) {
        Set<DataDefinitionsDeclaration> stepFreeInputs = getStepFreeInputs(step);
        for(int i=0;i<step.getIndex();i++){
            StepUsageDecleration currStep = steps.get(i);
            stepFreeInputs.stream().filter(input-> currStep.getStepDefinition().getOutputs().contains(input))
                    .forEach((input) -> {
                        step.addInputToMap(input.getName(), currStep, input.getName());
                        currStep.addOutputToMap(input.getAliasName(),step.getStepFinalName(),input.getName());});

        }

    }


    /**
     * create a set of a step freeInputs as defined.
     * @param step the step to get from it the free inputs
     * @return a set of DataDefinitionDeclarations.
     */
    private static Set<DataDefinitionsDeclaration> getStepFreeInputs(StepUsageDecleration step) {
        return step.getStepDefinition().getInputs().stream()
                .filter(input -> step.getInputRef(input.getFullQualifiedName()) == null && !input.isInitial())
                .collect(Collectors.toSet());
    }


    /**
     * creates a new stepUsage in the system, using the stepInFlow instance
     * if there is invalid data in the stepInFlow, adds the problem to problems and returns null.
     * @param stepInFlow a step that has been read by the flowReader to a StepInFlow instance.
     * @return a new instance of StepUsage if stepInFlow valid, else false.
     */

    private StepUsageDecleration createNewStepUsage(StepInFlow stepInFlow){
        StepDefinitionRegistry stepDefinitionRegistry = getStepDefinitionByName(stepInFlow.getName());
        if(stepDefinitionRegistry == null){
            problems.add("The following step is not exist: " + stepInFlow.getName());
            return null;
        }
        if(stepInFlow.getAlias() != null && stepInFlow.isContinueIfFailing() != null){
            return new StepUsageDeclerationImpl(stepInFlow.getAlias(),
                    new StepBuilder().getStepInstance(stepDefinitionRegistry.getStepDefinition().getClass().getName()),
                    stepInFlow.isContinueIfFailing(), stepInFlow.getNumOfStep() );
        }
        else if(stepInFlow.getAlias() != null){
            return new StepUsageDeclerationImpl(stepInFlow.getAlias(),
                    new StepBuilder().getStepInstance(stepDefinitionRegistry.getStepDefinition().getClass().getName()),
                    stepInFlow.getNumOfStep());
        }
        else if (stepInFlow.isContinueIfFailing() != null){
            return new StepUsageDeclerationImpl(
                    new StepBuilder().getStepInstance(stepDefinitionRegistry.getStepDefinition().getClass().getName()),
                    stepInFlow.isContinueIfFailing(), stepInFlow.getNumOfStep());
        }
        else{
            return new StepUsageDeclerationImpl(
                    new StepBuilder().getStepInstance(stepDefinitionRegistry.getStepDefinition().getClass().getName()),
                    stepInFlow.getNumOfStep());
        }
    }

    /**
     * creates all steps in the system.
     * if and when finds a step name that isn't in system, breaks after adding the problem to the problems.
     */
    private void buildStepUsages() {
        boolean flag = true;
        for (StepInFlow stepInFlow : flow.getStepsInFlow().getStepsInFlowList()) {
            StepUsageDecleration stepUsage = createNewStepUsage(stepInFlow);
            if (stepUsage == null) {
                flag = false;
                break; // stop iteration if a step is not found
            }
            steps.add(stepUsage);
        }
        if(flag) // then it means that all steps are in the system.
            steps.sort(Comparator.comparingInt(StepUsageDecleration::getIndex));

    }


    /**
     * gets a step name and returns a stepUsage matching to name.
     * if there isn't a step with that name, returns null and adds the problem to problem list.
     * @param stepName - a step name
     * @return StepDefinition registry that matches to name.
     */
    private StepDefinitionRegistry getStepDefinitionByName(String stepName){
//        NameToStep nameToStep=new NameToStepImpl();
//        return nameToStep.getDataDefinitionRegistry(stepName);
        for(StepDefinitionRegistry stepDefinition: StepDefinitionRegistry.values()){
            if (stepDefinition.getStepDefinition().getName().equals(stepName)){
                return stepDefinition;
            }
        }
        return null;
    }

    /**
     * @return all free inputs fo th flow
     */
    public Map<DataDefinitionsDeclaration, List<String>> getFreeInputsWithOptional() {
        return dataToRelatedSteps;
    }

    /**
     * update's all outputs of the system
     */
    private void updateAlloutputs(){
        allOuputs = steps.stream()
                .flatMap(step -> step.getStepDefinition().getOutputs().stream()
                        .map(dd -> new AbstractMap.SimpleEntry<>(dd.getAliasName(), new Pair<>(dd, step.getStepFinalName()))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, HashMap::new));
        this.updateFormalOutputs();
    }
    @Override
    public Map<String , Pair<DataDefinitionsDeclaration,String>> getAllOutputs() {
        return allOuputs;
    }

    private void updateFormalOutputs(){
        formalOuputs =Arrays.stream(flow.getFlowOutput().split(","))
                .filter(allOuputs::containsKey)
                .collect(Collectors.toMap(name -> name, allOuputs::get));

        if((Arrays.stream(flow.getFlowOutput().split(","))).collect(Collectors.toList()).size()!=formalOuputs.size())
        {
            for(String outputName:Arrays.stream(flow.getFlowOutput().split(",")).collect(Collectors.toList()))
            {
                if(!formalOuputs.containsKey(outputName))
                    problems.add("The output: "+outputName+" dosent exists in the flow.");
            }
        }
    }

    @Override
    public Map<String, Pair<DataDefinitionsDeclaration, String>> getFormalOuputs() {
        return formalOuputs;
    }

    @Override
    public Set<DataDefinitionsDeclaration> getFreeInputs() {
        return freeInputs;
    }
    @Override
    public Map<String, Pair<DataDefinitionsDeclaration, Object>> getInitialInputs() {
        return initialInputs;
    }

    private void updateAllDataDefinition(){
        for(StepUsageDecleration step:steps){
            for(DataDefinitionsDeclaration dd:step.getStepDefinition().getOutputs())
            {
                allDataDefinitions.put(dd.getAliasName(),dd);
            }
            for(DataDefinitionsDeclaration dd:step.getStepDefinition().getInputs())
            {
                allDataDefinitions.put(dd.getAliasName(),dd);
            }
        }
    }
    @Override
    public Map<String, DataDefinitionsDeclaration> getAllDataDefinitions() {
        return allDataDefinitions;
    }

    @Override
    public DataDefinitionsDeclaration getFreeInputByName(String sourceData) {
        for(DataDefinitionsDeclaration dd:freeInputs)
        {
            if(dd.getAliasName().equals(sourceData))
                return dd;
        }
        return null;
    }

    @Override
    public DataDefinitionsDeclaration getDDByName(String data) {
        return allDataDefinitions.get(data);
    }

    @Override
    public void addContinuationMapping(String source, String target) {
        continuationMapping.put(source,getDDByName(target).getFullQualifiedName());
    }

    @Override
    public Map<String, String> getContinuationMapping() {
        return continuationMapping;
    }
}
