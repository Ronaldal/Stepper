package StepperEngine.StepperReader.Exception;

public class ReaderException extends Exception{
    String filePath;

    public ReaderException(String message, String filePath){
        super(message);
        this.filePath = filePath;
    }

    @Override
    public String getMessage(){
        String message = super.getMessage() +" " +filePath + "\n";
        if(!filePath.endsWith("xml"))
            message = message.concat( "file is not xml");
        else{
            message = message.concat("file not found");
        }
        return message;
    }
}
