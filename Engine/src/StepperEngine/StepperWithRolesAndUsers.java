package StepperEngine;

import DTO.FlowDetails.FlowDetails;
import DTO.FlowExecutionData.FlowExecutionData;
import StepperEngine.Flow.api.FlowDefinition;
import StepperEngine.Flow.execute.FlowExecution;
import StepperEngine.Flow.execute.FlowExecutionWithUser;
import StepperEngine.exception.FlowNotAllowedException;
import users.roles.RoleDefinition;


import java.util.*;
import java.util.stream.Collectors;

public class StepperWithRolesAndUsers extends Stepper {

    public List<FlowDetails> getFlowsDetails(RoleDefinition ... roles) {
        Set<String> combinedFlows = getCombinedFlows(roles);
        return getFlowsDetails().stream().filter(flowDetails -> combinedFlows.contains(flowDetails.getFlowName())).collect(Collectors.toList());
    }

    public FlowDetails getFlowsDetailsByName(String flowName, RoleDefinition ... roles) throws FlowNotAllowedException{
        Set<String> combinedFlows = getCombinedFlows(roles);
        if(combinedFlows.contains(flowName)){
            return getFlowsDetailsByName(flowName);
        }
        throw new FlowNotAllowedException(flowName);
    }
    public List<String> getFlowNames(RoleDefinition ... roles) {
        Set<String> combinedFlows = getCombinedFlows(roles);
        return getFlowNames().stream().filter(combinedFlows::contains).collect(Collectors.toList());
    }

    public List<FlowExecutionData> getFlowExecutionDataList(String userName,RoleDefinition ... roles) {
        Set<String> combinedFlows = getCombinedFlows(roles);
        return getFlowExecutionDataList().stream().filter(flowExecutionData -> (combinedFlows.contains(flowExecutionData.getFlowName())) && flowExecutionData.getUserExecuted().equals(userName))
                .collect(Collectors.toList());
    }

    public List<FlowExecutionData> getFlowExecutionDataList(String username){
        return getFlowExecutionDataList().stream()
                .filter(flowExecutionData -> flowExecutionData.getUserExecuted().equals(username))
                .collect(Collectors.toList());
    }

    public Map<String, List<FlowExecutionData>> getFlowExecutionDataMap(RoleDefinition ... roles) {
        Set<String> combinedFlows = getCombinedFlows(roles);
        Map<String, List<FlowExecutionData>> flowExecutionDataMap = getFlowExecutionDataMap();
        Map<String, List<FlowExecutionData>> filteredMap = new HashMap<>();
        for(String key: flowExecutionDataMap.keySet()){
            if(combinedFlows.contains(key)){
                filteredMap.put(key, flowExecutionDataMap.get(key));
            }
        }
        return filteredMap;
    }
    public List<FlowExecutionData> getFlowExecutionsData(String flowName, RoleDefinition ... roles) throws FlowNotAllowedException {
        List<FlowExecutionData> flowExecutionData = getFlowExecutionDataMap(roles).get(flowName);
        if(flowExecutionData == null){
            throw new FlowNotAllowedException(flowName);
        }
        return flowExecutionData;
    }

    private static Set<String> getCombinedFlows(RoleDefinition[] roles) {
        Set<String> flowSet = new HashSet<>();
        Arrays.stream(roles).forEach(role->flowSet.addAll(role.getAllowedFlows()));
        return flowSet;
    }

//    public String createNewExecutionWithUser(String flowName, String username){
//        String uuid;
//        if (flowsMap.get(flowName) != null) {
//            FlowExecution flowExecution = new FlowExecutionWithUser(flowsMap.get(flowName), username);
//            uuid = flowExecution.getUUID();
//            executionsMap.put(flowExecution.getUUID(), flowExecution);
//        } else {
//            uuid = null;
//        }
//        return uuid;
//    }

    public String applyContinuation(String flowName, String uuid,String userName,Boolean isManager, RoleDefinition ... roles)throws FlowNotAllowedException{
        Set<String> combinedFlows = getCombinedFlows(roles);
        if(combinedFlows.contains(flowName)){
            FlowExecution pastFlow=executionsMap.get(uuid);
            FlowExecutionWithUser flowExecutionWithUser=new FlowExecutionWithUser(flowsMap.get(flowName),userName,isManager);
            flowExecutionWithUser.applyContinuation(pastFlow);
            executionsMap.put(flowExecutionWithUser.getUUID(),flowExecutionWithUser);
            return flowExecutionWithUser.getUUID();
            //return applyContinuation( uuid,flowName);
        }
        throw new FlowNotAllowedException(flowName);
    }

    public String getExecutionUsername(String uuid){
        FlowExecution flowExecutionByUuid = getFlowExecutionByUuid(uuid);
        if(flowExecutionByUuid instanceof FlowExecutionWithUser){
            FlowExecutionWithUser withUser = (FlowExecutionWithUser) flowExecutionByUuid;
            return withUser.getUserExecuting();
        }
        return null;
    }

    public String createNewExecution(String flowName, String username,Boolean isManager) {
        FlowDefinition flowDefinition = flowsMap.get(flowName);
        if(flowDefinition != null){
            FlowExecutionWithUser flowExecutionWithUser = new FlowExecutionWithUser(flowDefinition, username,isManager);
            String uuid = flowExecutionWithUser.getUUID();
            executionsMap.put(uuid, flowExecutionWithUser);
            return uuid;
        }else{
            return null;
        }
    }
}
