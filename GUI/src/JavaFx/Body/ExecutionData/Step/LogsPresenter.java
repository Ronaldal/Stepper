package JavaFx.Body.ExecutionData.Step;

public class LogsPresenter {
    private String time;
    private String context;

    public LogsPresenter(String time, String context) {
        this.time = time;
        this.context = context;
    }

    public String getTime() {
        return time;
    }

    public String getContext() {
        return context;
    }
}
