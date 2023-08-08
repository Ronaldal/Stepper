package StepperEngine.StepperReader.XMLReadClasses;

import generated.STContinuationMapping;

import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

public class ContinuationMapping implements Serializable {
    private String targetData;
    private String sourceData;
    public ContinuationMapping(STContinuationMapping stContinuationMapping){
        targetData=stContinuationMapping.getTargetData();
        sourceData=stContinuationMapping.getSourceData();
    }

    public String getTargetData() {
        return targetData;
    }

    public String getSourceData() {
        return sourceData;
    }
}
