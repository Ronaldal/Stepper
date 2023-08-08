package Managers;

import DTO.UserData;
import users.roles.RoleDefinition;
import users.roles.RoleImpl;

import java.util.*;

public class UserDataManager {
    private Map<String,Integer> userVersion=new HashMap<>();
    private Map<String, UserData> userData=new HashMap<>();
    private Set<String> deletedusers=new HashSet<>();
    private int n=0;

    public Set<String> getDeletedusers() {
        return deletedusers;
    }

    public synchronized void addUser(String userName){
        if(userName.equals("a"))
            n++;
        if(n==2)
            System.out.println("a");
        userVersion.put(userName,0);
        userData.put(userName,new UserData(userName));
    }

    public synchronized void addRoles(String userName, RoleImpl role){
        UserData userDataToUpdate = userData.get(userName);
        role.getAllowedFlows().stream().forEach(userDataToUpdate::addFlow);
        userDataToUpdate.addRole(role.getName());
        addVersion(userName);
    }
    public synchronized void removeRoles(String userName, RoleImpl role, Set<RoleDefinition> allowedRoles){
        UserData userDataToUpdate = userData.get(userName);
        role.getAllowedFlows().stream().forEach(userDataToUpdate::removeFlow);
        userDataToUpdate.removeRole(role.getName());
        allowedRoles.stream().forEach(roleDefinition -> roleDefinition.getAllowedFlows().stream().forEach(userDataToUpdate::addFlow));
        addVersion(userName);
    }
    public synchronized void removeUser(String userName){
        userData.remove(userName);
        userVersion.remove(userName);
        deletedusers.add(userName);
    }

    public synchronized Map<String, Integer> getUserVersion() {
        return userVersion;
    }

    public synchronized Map<String, UserData> getUserData() {
        return userData;
    }
    public synchronized void setAsManager(String userName) {
        userData.get(userName).setManager(true);
    }
    public synchronized void enableManager(String userName) {
        userData.get(userName).setManager(false);
    }

    public synchronized void addExecutionToUser(String username) {
        userData.get(username).addExecution();
        addVersion(username);
    }
    public synchronized void addVersion(String userName){
        int i = userVersion.get(userName) + 1;
        userVersion.put(userName,i);
    }
    public synchronized void resetDeltedUsers(){
        deletedusers=new HashSet<>();
    }
}
