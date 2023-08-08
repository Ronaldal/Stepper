package DTO.FlowDetails.StepDetails.FlowIODetails;

/**
 * holds details of a single input/output in a certain flow.
 */
public class FlowIODetails {

    private final String dataName;

    private final String typeName;
    private final String fullQualifideName;



    public FlowIODetails(String dataName, String typeName, String fullQualifideName) {
        this.dataName = dataName;
        this.typeName = typeName;
        this.fullQualifideName = fullQualifideName;
    }

    public String getFullQualifideName() {
        return fullQualifideName;
    }

    public String getDataName() {
        return dataName;
    }



    public String getTypeName() {
        return typeName;
    }




}
