package users;

import users.roles.RoleDefinition;
import users.roles.RoleImpl;

import java.util.*;

public class UserManager {

    private final Set<String> usersSet;
    private final Map<String, Set<RoleDefinition>> userRolesMap;
    private final Set<String> managers;
    public UserManager() {
        usersSet = new HashSet<>();
        userRolesMap=new HashMap<>();
        managers=new HashSet<>();
    }
    public synchronized void addRoleToUser(String userName,RoleImpl role){
        userRolesMap.get(userName).add(role);
    }
    public synchronized void removeRoleFromUser(String userName,RoleImpl role){
        userRolesMap.get(userName).remove(role);
    }

    public synchronized void addUser(String username) {
        usersSet.add(username);
        userRolesMap.put(username,new HashSet<>());
    }
    public synchronized void addManager(String username) {
        managers.add(username);
    }
    public synchronized void removeManager(String username) {
        managers.remove(username);
    }

    public synchronized void removeUser(String username) {
        usersSet.remove(username);
        userRolesMap.remove(username);
    }
    public synchronized boolean isUserManager(String username) {
        return managers.contains(username);
    }

    public synchronized Set<String> getUsers() {
        return Collections.unmodifiableSet(usersSet);
    }

    public synchronized Set<RoleDefinition> getUserRoles(String userName) {
        return userRolesMap.get(userName);
    }

    public boolean isUserExists(String username) {
        return usersSet.contains(username);
    }


}
