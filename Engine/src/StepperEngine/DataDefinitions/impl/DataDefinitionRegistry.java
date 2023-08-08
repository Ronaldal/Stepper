package StepperEngine.DataDefinitions.impl;

import StepperEngine.DataDefinitions.api.DataDefinition;
import StepperEngine.DataDefinitions.impl.List.StepperFilesList;
import StepperEngine.DataDefinitions.impl.List.StepperList;
import StepperEngine.DataDefinitions.impl.List.StepperStringsList;
import StepperEngine.DataDefinitions.impl.Mapping.StepperMapping;
import StepperEngine.DataDefinitions.impl.Mapping.StepperNumberMapping;
import StepperEngine.DataDefinitions.impl.Relation.SteeperRelation;
import StepperEngine.DataDefinitions.impl.Relation.StepperRelationString;

public enum DataDefinitionRegistry implements DataDefinition {
    STRING(new StepperString()),
    FILE_PATH(new StepperFilePath()),
    FOLDER_PATH(new StepperFolderPath()),
    DOUBLE(new StepperDouble()),
    FILE(new StepperFile()),
    LIST(new StepperList()),
    FILES_LIST(new StepperFilesList()),
    STRINGS_LIST(new StepperStringsList()),
    NUMBER(new StepperNumber()),
    MAPPING(new StepperMapping()),
    NUMBER_MAPPING(new StepperNumberMapping()),
    RELATION(new SteeperRelation()),
    RELATION_STRING(new StepperRelationString()),
    ZIPPER_ENUMERATION(new StepperZipperEnumeration()),
    JSON(new StepperJSON()),
    PROTOCOL_ENUMERATION(new StepperProtocolEnumerator()),
    METHOD_ENUMERATION(new StepperMethodEnumerator());

    DataDefinitionRegistry(DataDefinition dataDefinitionInterface){
        this.dataDefinitionInterface=dataDefinitionInterface;
    }
    private final DataDefinition dataDefinitionInterface;

    @Override
    public boolean isUserFriendly() {
        return dataDefinitionInterface.isUserFriendly();
    }

    @Override
    public String getName() {
        return dataDefinitionInterface.getName();
    }

    @Override
    public Class<?> getType() {
        return dataDefinitionInterface.getType();
    }
}
