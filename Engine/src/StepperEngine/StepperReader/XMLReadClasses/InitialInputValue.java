package StepperEngine.StepperReader.XMLReadClasses;

import generated.STInitialInputValue;

import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

public class InitialInputValue implements Serializable {
    private String inputName;
    private String initialValue;
    public InitialInputValue(STInitialInputValue stInitialInputValue){
        inputName=stInitialInputValue.getInputName();
        initialValue=stInitialInputValue.getInitialValue();
    }

    public String getInputName() {
        return inputName;
    }

    public String getInitialValue() {
        return initialValue;
    }
}
