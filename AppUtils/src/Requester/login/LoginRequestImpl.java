package Requester.login;

import Utils.Constants;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class LoginRequestImpl implements LoginRequest{

    @Override
    public Request login(String username) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.BASE_URL + Constants.LOGIN_PAGE).newBuilder();
        urlBuilder.addQueryParameter("username", username);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }

    @Override
    public Request getUsername() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.BASE_URL + Constants.LOGIN_PAGE).newBuilder();
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }
}
