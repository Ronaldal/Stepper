package Requester.login;

import okhttp3.Request;

public interface LoginRequest {

    Request login(String username);

    Request getUsername();

}
