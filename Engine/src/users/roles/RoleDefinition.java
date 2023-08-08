package users.roles;

import java.util.Collection;
import java.util.List;

public interface RoleDefinition {
    String getName();
    String getDescription();
    List<String> getAllowedFlows();
    void addFlow(String flowName);
    void addUser(String userName);
    Collection<String> getUsers();
    void removeUser(String userName);
}
