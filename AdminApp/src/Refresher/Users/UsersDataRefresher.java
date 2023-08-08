package Refresher.Users;

import AdminUtils.AdminUtils;
import Utils.Utils;
import com.google.gson.Gson;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

import static AdminUtils.AdminUtils.USERS_REQUESTER;


public class UsersDataRefresher extends TimerTask {
    private Map<String,Integer> usersVersion=new HashMap<>();
    private final Consumer<UsersAndVersion> consumer;

    public UsersDataRefresher( Consumer<UsersAndVersion> consumer) {
        this.consumer = consumer;
    }

    public void setUsersVersion(Map<String, Integer> usersVersion) {
        this.usersVersion = usersVersion;
    }

    @Override
    public void run() {
        Utils.runAsync(USERS_REQUESTER.getUsersData(usersVersion),
                new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        System.out.println("error");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try (ResponseBody responseBody = response.body()) {
                                Gson gson = new Gson();
                                UsersAndVersion usersAndVersion = gson.fromJson(responseBody.string(), UsersAndVersion.class);
                                Platform.runLater(() -> consumer.accept(usersAndVersion));
                            } catch (IOException e) {
                                System.out.println("Error processing response: " + e.getMessage());
                            }

                        }
                    }
                },
                AdminUtils.HTTP_CLIENT);
    }
}
