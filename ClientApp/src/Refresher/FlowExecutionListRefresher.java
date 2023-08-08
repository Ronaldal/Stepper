package Refresher;

import DTO.FlowExecutionData.FlowExecutionData;
import Requester.execution.ExecutionRequestImpl;
import Utils.Constants;
import Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static JavaFx.ClientUtils.HTTP_CLIENT;

public class FlowExecutionListRefresher extends TimerTask {

    private final Consumer<List<FlowExecutionData>> consumer;

    public FlowExecutionListRefresher(Consumer<List<FlowExecutionData>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        Utils.runAsync(new ExecutionRequestImpl().executionDataList(),
                new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        System.out.println("error");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            if (response.isSuccessful()) {
                                Gson gson = Constants.GSON_INSTANCE;
                                Type listType = new TypeToken<List<FlowExecutionData>>() {
                                }.getType();
                                List<FlowExecutionData> flowExecutionDataList = gson.fromJson(responseBody.string(), listType);
                                Platform.runLater(() -> consumer.accept(flowExecutionDataList));
                            }
                        }catch (IOException e) {
                            System.out.println("Error processing response: " + e.getMessage());
                        }
                    }
                },
                HTTP_CLIENT);
    }
}
