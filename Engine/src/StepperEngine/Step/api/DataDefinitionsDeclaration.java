package StepperEngine.Step.api;

import StepperEngine.DataDefinitions.api.DataDefinition;

public interface DataDefinitionsDeclaration {
    void setInitial(boolean initial);
    boolean isInitial();
    String getName();
    String getAliasName();
    String getFullQualifiedName();
    String userString();

    DataNecessity necessity();

    DataDefinition dataDefinition();

    boolean equals(Object obj);

    void setAliasName(String alias);
}
