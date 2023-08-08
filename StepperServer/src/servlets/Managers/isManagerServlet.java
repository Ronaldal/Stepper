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

@WebServlet(name = "isManager", urlPatterns = "/users/isManager")
public class isManagerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String userName=req.getParameter(USERNAME_PARAMETER);
        if(userName!=null){
            boolean res=userManager.isUserManager(userName);
            ServletUtils.sendResponse(res, Boolean.class,resp);
        }else {
            ServletUtils.sendBadRequest(resp,"need to add user name parameter");
        }
    }
}
