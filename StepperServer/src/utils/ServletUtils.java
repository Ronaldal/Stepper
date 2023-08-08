package utils;

import DTO.DataAndType;
import Managers.StatsManager;
import Managers.UserDataManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import exceptions.MissingParamException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UserManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServletUtils {

    public final static String FLOW_NAME_PARAMETER = "flow_name";
    public final static String ROLE_NAME_PARAMETER = "role_name";
    public final static String UUID_PARAMETER = "uuid";
    public final static String USERNAME_PARAMETER = "username";
    private static final String STATS_MANAGER_ATTRIBUTE_NAME = "statsManager";
    private static final Object statsManagerLock = new Object();
    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String USER_DATA_ATTRIBUTE = "userDataManager";
    private static final Object userManagerLock = new Object();
    private static final Object userDataManagerLock = new Object();
    public static final String ADMIN_USERNAME = "admin";

    public static final String NOT_LOGIN_ERROR_MESSAGE = "please login for this request";

    private static Boolean isAdminLoggedIn = false;


    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static void setAdminLoggedIn(Boolean isAdminLoggedIn) {
        ServletUtils.isAdminLoggedIn = isAdminLoggedIn;
    }

    public static Boolean getIsAdminLoggedIn() {
        return isAdminLoggedIn;
    }

    public final static Gson GSON_INSTANCE;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DataAndType.class, new DataAndType.DataAndTypeAdapter());
        GSON_INSTANCE = gsonBuilder.create();
    }


    public static void sendBadRequest(HttpServletResponse response, String message) throws IOException {
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("message", message);
// Set the response status code and content type
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
// Write the JSON object as the response
        Gson gson = new Gson();
        String json = gson.toJson(errorJson);
        response.getWriter().write(json);
    }

    public static void sendNotLoggedInBadRequest(HttpServletResponse response) throws IOException {
        sendBadRequest(response, NOT_LOGIN_ERROR_MESSAGE);
    }

    public static void sendFlowNotAllowedBadRequest(HttpServletResponse response, String username, String flowName) throws IOException {
        sendBadRequest(response, String.format("user %s cannot access flow %s", username, flowName));
    }

    public static void sendExecutionNotAllowedBadRequest(HttpServletResponse response, String username, String uuid) throws IOException {
        sendBadRequest(response, String.format("user %s cannot access execution %s", username, uuid));
    }

    public static void sendRoleNotMatchedBadRequest(HttpServletResponse response, String username) throws IOException{
        sendBadRequest(response, String.format("user %s don't have at least one role to see the requested flow/s"));
    }

    public static void sendResponse(Object obj, Class<?> expectedClass, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(obj, expectedClass);
        response.getWriter().write(jsonResponse);
    }

    public static String getMissingParameterMessage(String param_name){
        return String.format("missing parameter: %s", param_name);
    }

    public static Map<String, String> getParamMap(HttpServletRequest request, String ... params) throws MissingParamException {
        Map<String,String> result = new HashMap<>();
        for(String param:params){
            String currParamData = request.getParameter(param);
            if(currParamData == null){
                throw new MissingParamException(param);
            }
            result.put(param, currParamData);
        }
        return result;
    }

    public static StatsManager getStatsManager(ServletContext servletContext) {
        synchronized (statsManagerLock) {
            if (servletContext.getAttribute(STATS_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(STATS_MANAGER_ATTRIBUTE_NAME, new StatsManager());
            }
        }

        return (StatsManager) servletContext.getAttribute(STATS_MANAGER_ATTRIBUTE_NAME);
    }
    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return Integer.MIN_VALUE;
    }
    public static UserDataManager getUserDataManager(ServletContext servletContext){
        synchronized (userDataManagerLock){
            if(servletContext.getAttribute(USER_DATA_ATTRIBUTE)==null){
                servletContext.setAttribute(USER_DATA_ATTRIBUTE,new UserDataManager());
            }
        }
        return (UserDataManager) servletContext.getAttribute(USER_DATA_ATTRIBUTE);
    }
}
