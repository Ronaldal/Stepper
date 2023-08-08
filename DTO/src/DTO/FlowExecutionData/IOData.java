package DTO.FlowExecutionData;

import StepperEngine.DataDefinitions.List.FilesListDataDef;
import StepperEngine.DataDefinitions.List.StringListDataDef;
import StepperEngine.DataDefinitions.Relation.RelationOfStringRows;
import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.List;

/**
 * holds an input/output data from a flow that has been executed.
 */
public class IOData implements Serializable {

    private final boolean isOutput;
    private final String name;
    private final String type;
    private final String content;
    private final String necessity;
    private final String userString;
    private  Object value;
    private List<String> listOfString=null;
    private List<List<String>> rows =null;
    private JsonElement jsonElement=null;


    private final String FullQualifiedName;


    public IOData(boolean isOutput, String name, String userString, String type, String content, String necessity, Object value, String fullQualifiedName) {
        this.isOutput = isOutput;
        this.name = name;
        this.userString = userString;
        this.type = type;
        this.content = content;
        this.necessity = necessity;
        this.value=value;
        getDDValues(type);
        FullQualifiedName = fullQualifiedName;
    }

    private void getDDValues(String type) {
        switch (type) {
            case "FilesListDataDef":
                FilesListDataDef dataValue = getDataValue(FilesListDataDef.class);
                listOfString=dataValue.getPaths();
                break;
            case "StringListDataDef":
                StringListDataDef stringListDataDef=getDataValue(StringListDataDef.class);
                listOfString=stringListDataDef.getStringList();
                break;
            case "RelationOfStringRows":
                RelationOfStringRows relation=getDataValue(RelationOfStringRows.class);
                listOfString=relation.getColNames();
                this.rows = relation.getRows();
                break;
            case "JsonElement":
                jsonElement=getDataValue(JsonElement.class);
                break;
            default:
                break;
        }

    }

    public String getFullQualifiedName() {
        return FullQualifiedName;
    }

    public <T> T getDataValue (Class<T> exceptedDataType) {
        return exceptedDataType.cast(value);
    }


    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public boolean isOutput() {
        return isOutput;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getNecessity() {
        return necessity;
    }

    public String getUserString() {
        return userString;
    }

    public List<String> getList() {
        return listOfString;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public JsonElement getJsonElement() {
        return jsonElement;
    }

}
