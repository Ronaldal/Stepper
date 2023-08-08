package users.roles;

import java.util.*;

public class RoleImpl implements RoleDefinition{
    private String name;
    private String description;
    private List<String> flows;
    private Set<String> users=new HashSet<>();

    public RoleImpl() {
        this.name = "";
        this.description = "";
        this.flows = new ArrayList<>();
    }
    public RoleImpl(String name, String description, List<String> flows) {
        this.name = name;
        this.description = description;
        this.flows = flows;
    }

    public RoleImpl(RoleImpl newRole) {
        this.name = newRole.name;
        this.description = newRole.description;
        this.flows =new ArrayList<>( newRole.flows);
        this.users=new HashSet<>(newRole.users);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<String> getAllowedFlows() {
        return flows;
    }

    @Override
    public void addFlow(String flowName) {
        flows.add(flowName);
    }

    @Override
    public void addUser(String userName) {
        users.add(userName);
    }

    @Override
    public Collection<String> getUsers() {
        return users;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFlows(List<String> flows) {
        this.flows = flows;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    public void removeUser(String userName){ users.remove(userName);}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleImpl role = (RoleImpl) o;
        return name.equals(role.name) && description.equals(role.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
