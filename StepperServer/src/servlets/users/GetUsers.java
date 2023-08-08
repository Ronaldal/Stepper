package servlets.users;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UserManager;
import utils.ServletUtils;
import java.io.IOException;

import java.util.Set;

@WebServlet(name = "getAllUsers", urlPatterns = "/users/getUsers")
public class GetUsers extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Set<String> users = userManager.getUsers();
        ServletUtils.sendResponse(users,users.getClass(),resp);
    }
}
