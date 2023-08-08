package servlets.Managers;

import Managers.UserDataManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UserManager;
import utils.ServletUtils;

import java.io.IOException;

import static utils.ServletUtils.USERNAME_PARAMETER;

@WebServlet(name = "manager", urlPatterns = "/users/manager")

public class ManagerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        UserDataManager userDataManager = ServletUtils.getUserDataManager(getServletContext());
        String userName=req.getParameter(USERNAME_PARAMETER);
        if(userName!=null){
            userManager.addManager(userName);
            userDataManager.setAsManager(userName);
            ServletUtils.sendResponse(true, Boolean.class,resp);
        }
        else {
            ServletUtils.sendBadRequest(resp,"need to add user name parameter");
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        UserDataManager userDataManager = ServletUtils.getUserDataManager(getServletContext());
        String userName=req.getParameter(USERNAME_PARAMETER);
        if(userName!=null){
            userManager.removeManager(userName);
            userDataManager.enableManager(userName);
            ServletUtils.sendResponse(true, Boolean.class,resp);
        }
        else {
            ServletUtils.sendBadRequest(resp,"need to add user name parameter");
        }

    }
}
