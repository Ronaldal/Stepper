package exceptions;

public class MissingParamException extends RuntimeException{

    private final String missingParamName;

    public MissingParamException(String paramName) {
        this.missingParamName = paramName;
    }

    public String getMissingParamName() {
        return missingParamName;
    }
}
