package servlets.users;

import DTO.UserData;
import Managers.UserDataManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.*;

@WebServlet(name = "getAllUsersData", urlPatterns = "/users/getUsersData")
public class GetUsersData extends HttpServlet {
    private final String VERSION_MAP_PARAMETER="users_version";


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonData = req.getParameter(VERSION_MAP_PARAMETER);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> versionMap = gson.fromJson(jsonData, type);
        synchronized (getServletContext()) {
            UserDataManager userDataManager = ServletUtils.getUserDataManager(getServletContext());
            Map<String, Integer> userVersion = userDataManager.getUserVersion();
            List<UserData> userDataList = new ArrayList<>();
            for (String userName : userVersion.keySet()) {
                if (versionMap!=null && versionMap.containsKey(userName)) {
                    if (userVersion.get(userName) != versionMap.get(userName)) {
                        userDataList.add(userDataManager.getUserData().get(userName));
                    }
                } else {
                    userDataList.add(userDataManager.getUserData().get(userName));
                }
            }
            UsersAndVersion newData=new UsersAndVersion(userDataList,userVersion, userDataManager.getDeletedusers());
            if(!userDataManager.getDeletedusers().isEmpty())
                userDataManager.resetDeltedUsers();
            String jsonResponse = gson.toJson(newData);
            try (PrintWriter out = resp.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        }

    }
    private static class UsersAndVersion {

        final private List<UserData> entries;
        final private  Map<String, Integer> userVersion;
        final private Set<String> deletedusers;

        public UsersAndVersion(List<UserData> entries, Map<String, Integer> userVersion, Set<String> deletedusers) {
            this.entries = entries;
            this.userVersion = userVersion;
            this.deletedusers = deletedusers;
        }
    }

}
