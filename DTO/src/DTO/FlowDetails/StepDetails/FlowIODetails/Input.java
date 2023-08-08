package DTO.FlowDetails.StepDetails.FlowIODetails;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Input extends FlowIODetails {

    private final String necessity;
    private final String relatedStepsString;
    private final String connectedToStep;
    private final String formOutput;
    private final List<String> relatedSteps;
    private final String userString;


    public Input(String dataName, String typeName, String necessity, List<String> relatedSteps, String userString, String fullQualifideName) {
        super(dataName, typeName,fullQualifideName);
        this.necessity = necessity;
        this.relatedSteps = relatedSteps;
        this.userString = userString;
        relatedStepsString=relatedSteps.stream().collect(Collectors.joining(" , "));
        connectedToStep="";
        formOutput="";
    }
    public Input(String dataName, String typeName, String necessity, Pair<String,String> fromStepsData, String userString, String fullQualifideName) {
        super(dataName, typeName,fullQualifideName);
        this.necessity = necessity;
        this.userString = userString;
        this.relatedSteps = new ArrayList<>();
        relatedStepsString=relatedSteps.stream().collect(Collectors.joining(" , "));
        connectedToStep=fromStepsData.getKey();
        formOutput=getBeforeDot(fromStepsData);
    }

    private static String getBeforeDot(Pair<String, String> fromStepsData) {
        int dotIndex = fromStepsData.getValue().indexOf('.');
        String beforeDot;
        if (dotIndex != -1) {
            beforeDot = fromStepsData.getValue().substring(0, dotIndex);
        } else {
            beforeDot = fromStepsData.getValue();
        }
        return beforeDot;
    }



    public String getUserString() {
        return userString;
    }

    public String getConnectedToStep() {
        return connectedToStep;
    }

    public String getFormOutput() {
        return formOutput;
    }

    public String getNecessity() {
        return necessity;
    }

    public List<String> getRelatedSteps() {
        return relatedSteps;
    }
    public String getRelatedStepsString() {
        return relatedStepsString;
    }
}
