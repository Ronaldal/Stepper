package users.roles;

import DTO.FlowDetails.FlowDetails;
import StepperEngine.Stepper;
import StepperEngine.StepperWithRolesAndUsers;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RolesManager {
    private final Map<String, RoleImpl> roleMap=new HashMap<>();
    private final Map<String, Set<RoleDefinition>> userToRolesMap = new HashMap<>();
    public static final String READ_ONLY_FLOWS = "Read Only Flows";
    public static final String ALL_FLOWS_ROLE = "All flows";

    public RolesManager(Stepper stepper){
        addReadOnlyRole(stepper);
        addAllFlowsRole(stepper);
    }

    public void addReadOnlyRole(Stepper stepper) {
        List<String> flows = getReadOnlyFlowsName(stepper);
        roleMap.put(READ_ONLY_FLOWS,new RoleImpl(READ_ONLY_FLOWS,"Just read only flow's belong to this role", flows));

    }

    @NotNull
    private static List<String> getReadOnlyFlowsName(Stepper stepper) {
        List<String> flows=new ArrayList<>();
        for(FlowDetails flow: stepper.getFlowsDetails())
        {
            if(flow.isFlowReadOnly())
                flows.add(flow.getFlowName());

        }
        return flows;
    }

    public void addAllFlowsRole(Stepper stepper) {
        roleMap.put(ALL_FLOWS_ROLE,new RoleImpl(ALL_FLOWS_ROLE,"The role contains all flows in system", stepper.getFlowNames()));
    }
    public synchronized RoleImpl getDefaultRole(){
        return roleMap.get(READ_ONLY_FLOWS);
    }

    public Map<String, RoleImpl> getRoleMap() {
        return roleMap;
    }
    public synchronized void addRole(RoleImpl role){
        roleMap.put(role.getName(),role);
    }
    public synchronized void addUserToRole(String roleName,String userName){
        RoleDefinition role = roleMap.get(roleName);
        role.addUser(userName);
        userToRolesMap.computeIfAbsent(userName, key->new HashSet<>()).add(role);
    }
    public List<RoleImpl> updateDefaultRoles(StepperWithRolesAndUsers stepper){
        List<RoleImpl> res=new ArrayList<>();
        res.add(addNewRolesToReadOnly(stepper));
        res.add(addNewRolesToAllFlowRole(stepper));
        return res;
    }

    private RoleImpl addNewRolesToAllFlowRole(StepperWithRolesAndUsers stepper){
        roleMap.get(ALL_FLOWS_ROLE).setFlows(stepper.getFlowNames());
        return roleMap.get(ALL_FLOWS_ROLE);
    }
    private RoleImpl addNewRolesToReadOnly(StepperWithRolesAndUsers stepper){
        roleMap.get(READ_ONLY_FLOWS).setFlows(getReadOnlyFlowsName(stepper));
        return roleMap.get(READ_ONLY_FLOWS);
    }



    public Set<RoleDefinition> getUserRoles(String username){
        return userToRolesMap.get(username);
    }

    public void changeRoles(RoleImpl newRole, RoleImpl oldRole) {
        roleMap.remove(oldRole.getName());
        addRole(newRole);
    }
    public void removeRole(String roleName){
        roleMap.remove(roleName);
    }
}
