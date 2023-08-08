package Requester.Roles;

import Utils.Constants;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import users.roles.RoleImpl;

import static Utils.Constants.REMOVE_ROLE;

public class RoleRequestImpl implements RoleRequest{
    private final String GET_ROLE_URL= Constants.BASE_URL+Constants.GET_ROLE;
    private final String PUT_ROLE_URL= Constants.BASE_URL+Constants.PUT_ROLE;
    private final String GET_USER_ROLES_URL = Constants.BASE_URL + Constants.GET_USER_ROLES_URL;
    private final String REMOVE_ROLES_URL = Constants.BASE_URL + REMOVE_ROLE ;
    @Override
    public Request getAllRoles() {
        return new Request.Builder()
                .url(GET_ROLE_URL)
                .get()
                .build();
    }

    @Override
    public Request getRole(String roleName) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GET_ROLE_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.ROLE_NAME_PARAMETER, roleName);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }

    @Override
    public Request addRole(RoleImpl role) {
        String newRoleJson = Constants.GSON_INSTANCE.toJson(role);
        String combinedJson = "{\"newRole\":" + newRoleJson +"}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), combinedJson);
        return new Request.Builder()
                .url(PUT_ROLE_URL)
                .put(requestBody)
                .build();
    }

    @Override
    public Request changeRole(RoleImpl newRole,RoleImpl oldRole) {
        String newRoleJson = Constants.GSON_INSTANCE.toJson(newRole);
        String oldRoleJson = Constants.GSON_INSTANCE.toJson(oldRole);
        String combinedJson = "{\"newRole\":" + newRoleJson + ", \"oldRole\":" + oldRoleJson + "}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), combinedJson);
        return new Request.Builder()
                .url(PUT_ROLE_URL)
                .put(requestBody)
                .build();
    }

    @Override
    public Request removeRole(String roleName) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(REMOVE_ROLES_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.ROLE_NAME_PARAMETER, roleName);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .delete()
                .url(url)
                .build();
    }

    @Override
    public Request getUserRoles() {
        return new Request.Builder()
                .url(GET_USER_ROLES_URL)
                .get()
                .build();
    }
}
