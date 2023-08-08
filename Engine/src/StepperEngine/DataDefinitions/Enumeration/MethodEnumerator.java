package StepperEngine.DataDefinitions.Enumeration;

public enum MethodEnumerator {
    GET("GET"),
    PUT("PUT"),
    POST("POST"),
    DELETE("DELETE");

    private final String stringValue;

    MethodEnumerator(String stringValue) {
        this.stringValue = stringValue;
    }
    public String getStringValue() {
        return stringValue;
    }
    public static MethodEnumerator fromString(String value) throws IllegalArgumentException{
        for(MethodEnumerator methodEnumerator: MethodEnumerator.values()){
            if(methodEnumerator.stringValue.equalsIgnoreCase(value)){
                return methodEnumerator;
            }
        }
        throw new IllegalArgumentException("no method enumerator with string value: " + value);
    }
}
