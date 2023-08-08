package servlets.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.IOException;

import static utils.ServletUtils.ADMIN_USERNAME;
import static utils.SessionUtils.USERNAME;
@WebServlet(urlPatterns = "/admin/login")
public class AdminLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!ServletUtils.getIsAdminLoggedIn()) {
            req.getSession(true).setAttribute(USERNAME, ADMIN_USERNAME);
            ServletUtils.setAdminLoggedIn(true);
            ServletUtils.sendResponse(Boolean.TRUE, Boolean.class, resp);
        }else{
            ServletUtils.sendResponse(Boolean.FALSE, Boolean.class, resp);
        }
    }
}
