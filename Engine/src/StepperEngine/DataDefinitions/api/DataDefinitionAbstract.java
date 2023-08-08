package StepperEngine.DataDefinitions.api;

import java.util.Objects;

public abstract class DataDefinitionAbstract implements DataDefinition {

    String name;
    boolean isUserFriendly;
    Class<?> type;

    public DataDefinitionAbstract(String name, Boolean isUserFriendly, Class<?> type) {
        this.name = name;
        this.isUserFriendly = isUserFriendly;
        this.type = type;
    }

    @Override
    public boolean isUserFriendly() {
        return isUserFriendly;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !DataDefinitionAbstract.class.isAssignableFrom(o.getClass())) return false;
        DataDefinitionAbstract other = (DataDefinitionAbstract)o;
        return this.type.isAssignableFrom(other.type) || other.type.isAssignableFrom(this.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
