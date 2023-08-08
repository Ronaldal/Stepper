package DTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserData {
    private String userName;
    private Set<String> roles;
    private Set<String> flows;
    private Integer numOfExecutions;
    private boolean isManager;

    public UserData (String userName){
        this.userName=userName;
        roles=new HashSet<>();
        numOfExecutions=0;
        flows=new HashSet<>();
        isManager=false;
    }

    public synchronized void setUserName(String userName) {
        this.userName = userName;
    }

    public synchronized void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    public synchronized void addRole(String roleName){
        roles.add(roleName);
    }

    public synchronized void addFlow(String flowName){
        flows.add(flowName);
    }
    public synchronized void removeFlow(String flowName){
        flows.remove(flowName);
    }
    public synchronized Integer getNumOfFlow(){
        return flows.size();
    }

    public synchronized void setNumOfExecutions(int numOfExecutions) {
        this.numOfExecutions = numOfExecutions;
    }
    public synchronized void addExecution(){
        numOfExecutions++;
    }
    public synchronized void removeRole(String role){
        roles.remove(role);
    }

    public synchronized void setManager(boolean manager) {
        isManager = manager;
    }

    public synchronized String getUserName() {
        return userName;
    }

    public synchronized Set<String> getRoles() {
        return roles;
    }

    public synchronized Integer getNumOfExecutions() {
        return numOfExecutions;
    }

    public synchronized boolean isManager() {
        return isManager;
    }
}
