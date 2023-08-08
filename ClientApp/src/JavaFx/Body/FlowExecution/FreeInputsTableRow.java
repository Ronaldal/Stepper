package JavaFx.Body.FlowExecution;


import DTO.FlowDetails.StepDetails.FlowIODetails.Input;

public class FreeInputsTableRow {
    private String name;

    private String apiName;
    private String type;
    private String necessity;
    private String value;


    public FreeInputsTableRow(Input input, String value) {
        this.name = input.getUserString();
        this.apiName = input.getDataName();
        this.type = input.getTypeName();
        this.necessity = input.getNecessity();
        this.value = value;

    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getNecessity() {
        return necessity;
    }

    public String getValue() {
        return value;
    }

    public String getApiName() { return apiName; }
}
