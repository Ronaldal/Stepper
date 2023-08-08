package servlets.roles;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.roles.RoleImpl;
import users.roles.RolesManager;
import utils.ServletUtils;
import utils.StepperUtils;
import utils.Valitator.UserValidator;
import utils.Valitator.UserValidatorImpl;

import java.io.BufferedReader;
import java.io.IOException;


@WebServlet(name = "removeRole", urlPatterns = "/role/removeRole")
public class removeRole extends HttpServlet {
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserValidator userValidator = new UserValidatorImpl(req);
        if(!userValidator.isAdmin()){
            ServletUtils.sendBadRequest(resp, "Only admin has privilege for this action");
        }
        else {
           String roleName=req.getParameter("role_name");
           if(roleName!=null)
           {
               RolesManager rolesManger = StepperUtils.getRolesManger(getServletContext());
               rolesManger.removeRole(roleName);
               ServletUtils.sendResponse(true, Boolean.class,resp);
           }else {
               ServletUtils.sendBadRequest(resp,ServletUtils.getMissingParameterMessage("role_name"));
           }
        }
    }
}
