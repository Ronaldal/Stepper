package Requester.Users;

import Utils.Constants;
import com.google.gson.reflect.TypeToken;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.lang.reflect.Type;
import java.util.Map;

import static Utils.Constants.USERNAME_PARAMETER;

public class UsersRequesterImpl implements UsersRequester {
    private final String GET_USERS_URL= Constants.BASE_URL+Constants.GET_USERS;
    private final String GET_USERS_DATA_URL= Constants.BASE_URL+Constants.GET_USERS_DATA;
    private final String MANAGER_URL= Constants.BASE_URL+Constants.MANAGER;
    private final String IS_MANAGER_URL= Constants.BASE_URL+Constants.IS_MANAGER;
    private final String VERSION_MAP_PARAMETER="users_version";

    @Override
    public Request getUsers() {
        return new Request.Builder()
                .url(GET_USERS_URL)
                .get()
                .build();
    }

    @Override
    public Request getUsersData(Map<String, Integer> version){
        Type type = new TypeToken<Map<String, Integer>>(){}.getType();
        String data = Constants.GSON_INSTANCE.toJson(version, type);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GET_USERS_DATA_URL).newBuilder();
        urlBuilder.addQueryParameter(VERSION_MAP_PARAMETER,data);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }

    @Override
    public Request addManager(String userName){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(MANAGER_URL).newBuilder();
        urlBuilder.addQueryParameter(USERNAME_PARAMETER, userName);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }

    @Override
    public Request removeManager(String userName) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(MANAGER_URL).newBuilder();
        urlBuilder.addQueryParameter(USERNAME_PARAMETER, userName);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .delete()
                .url(url)
                .build();
    }
    @Override
    public Request isManager(String userName) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(IS_MANAGER_URL).newBuilder();
        urlBuilder.addQueryParameter(USERNAME_PARAMETER, userName);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }
}
