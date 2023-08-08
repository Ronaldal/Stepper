package StepperEngine.DataDefinitions.Enumeration;

public enum ProtocolEnumerator {
    HTTP("HTTP"),
    HTTPS("HTTPS");

    private final String stringValue;

    ProtocolEnumerator(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static ProtocolEnumerator fromString(String value) throws IllegalArgumentException{
        for(ProtocolEnumerator protocolEnumerator: ProtocolEnumerator.values()){
            if(protocolEnumerator.stringValue.equalsIgnoreCase(value)){
                return protocolEnumerator;
            }
        }
        throw new IllegalArgumentException("no protocol enumerator with string value: " + value);
    }
}
