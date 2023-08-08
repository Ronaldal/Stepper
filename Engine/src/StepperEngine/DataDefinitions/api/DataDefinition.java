package StepperEngine.DataDefinitions.api;

public interface DataDefinition {

    boolean isUserFriendly();
    String getName();

    Class<?> getType();
    boolean equals(Object o);

}
