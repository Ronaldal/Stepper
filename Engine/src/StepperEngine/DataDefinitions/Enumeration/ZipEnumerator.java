package StepperEngine.DataDefinitions.Enumeration;

public enum ZipEnumerator {
    ZIP("ZIP"),
    UNZIP("UNZIP");

    private final String stringValue;
    ZipEnumerator(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static ZipEnumerator fromString(String value) throws IllegalArgumentException{
        for(ZipEnumerator zipEnumerator: ZipEnumerator.values()){
            if(zipEnumerator.stringValue.equalsIgnoreCase(value)){
                return zipEnumerator;
            }
        }
        throw new IllegalArgumentException("no zip enumerator with string value: " + value);
    }
}
