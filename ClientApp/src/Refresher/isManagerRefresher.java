package Refresher;

import JavaFx.ClientUtils;
import Requester.Users.UsersRequesterImpl;
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

public class isManagerRefresher extends TimerTask {
    private final Consumer<Boolean> consumer;
    private final String userName;

    public isManagerRefresher(Consumer<Boolean> consumer, String userName) {
        this.consumer = consumer;
        this.userName = userName;
    }

    @Override
    public void run() {
        Utils.runAsync(new UsersRequesterImpl().isManager(userName), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("error");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try(ResponseBody body = response.body()) {
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        Type booleanType = new TypeToken<Boolean>() {
                        }.getType();
                        Boolean isManager = gson.fromJson(body.string(), booleanType);
                        Platform.runLater(() -> consumer.accept(isManager));
                    }
                }catch (IOException e) {
                    System.out.println("Error processing response: " + e.getMessage());
                }
            }
        }, ClientUtils.HTTP_CLIENT);

    }
}
