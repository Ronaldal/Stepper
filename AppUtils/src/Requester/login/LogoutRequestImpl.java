package Requester.login;

import Utils.Constants;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LogoutRequestImpl implements LogoutRequest{
    @Override
    public Request logoutRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.BASE_URL + Constants.LOGOUT_URL).newBuilder();
        String url = urlBuilder.build().toString();

        // For a DELETE request, we can set an empty request body or use null.
        // It's common to use an empty request body for DELETE requests.
        RequestBody requestBody = RequestBody.create(null, new byte[0]);

        return new Request.Builder()
                .url(url)
                .delete(requestBody)
                .build();
    }
}
