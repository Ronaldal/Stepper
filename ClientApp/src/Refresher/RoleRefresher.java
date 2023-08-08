package Refresher;

import DTO.FlowDetails.FlowDetails;
import JavaFx.ClientUtils;
import Requester.Roles.RoleRequestImpl;
import Utils.*;
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
import java.util.Collection;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class RoleRefresher extends TimerTask {
    private final Consumer<Collection<String>> consumer;

    public RoleRefresher(Consumer<Collection<String>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        Utils.runAsync(new RoleRequestImpl().getUserRoles(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("error");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try(ResponseBody body = response.body()) {
                    if (response.isSuccessful()) {
                        Gson gson = Constants.GSON_INSTANCE;
                        Type listType = new TypeToken<List<String>>() {
                        }.getType();
                        List<String> roles = gson.fromJson(body.string(), listType);
                        Platform.runLater(() -> consumer.accept(roles));
                    }
                }catch (IOException e) {
                    System.out.println("Error processing response: " + e.getMessage());
                }
            }
        }, ClientUtils.HTTP_CLIENT);
    }
}
