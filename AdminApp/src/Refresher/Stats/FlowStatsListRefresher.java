package Refresher.Stats;

import AdminUtils.AdminUtils;
import Utils.Constants;
import Utils.Utils;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static AdminUtils.AdminUtils.FLOW_STATS_REQUEST;

public class FlowStatsListRefresher extends TimerTask  {

    //private final Consumer<List<FlowExecutionStats>> consumer;
    private final Consumer<StatsWithVersion> consumer;
    private final IntegerProperty statsVersion;

    public FlowStatsListRefresher(Consumer<StatsWithVersion> consumer,IntegerProperty statsVersion) {
        this.consumer = consumer;
        this.statsVersion=statsVersion;
    }

    public void setStatsVersion(int statsVersion) {
        this.statsVersion.set(statsVersion);
    }


    @Override
    public void run() {
        Utils.runAsync(FLOW_STATS_REQUEST.getStatsByVersion(statsVersion.get()),
                new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        System.out.println("error");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Gson gson=Constants.GSON_INSTANCE;
                        StatsWithVersion statsWithVersion = gson.fromJson(response.body().string(), StatsWithVersion.class);
                        Platform.runLater(() -> consumer.accept(statsWithVersion));
                    }
                },
                AdminUtils.HTTP_CLIENT);
    }
}
