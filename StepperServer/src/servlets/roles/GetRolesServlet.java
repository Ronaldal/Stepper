package servlets.roles;

import StepperEngine.StepperWithRolesAndUsers;
import users.roles.RolesManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.roles.RoleImpl;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.StepperUtils;
import utils.Valitator.UserValidator;
import utils.Valitator.UserValidatorImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "getRoles", urlPatterns = "/role/getRoles")
public class GetRolesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserValidator userValidator = new UserValidatorImpl(req);
        if(!userValidator.isAdmin()){
            ServletUtils.sendBadRequest(resp, "Only admin has privilege for this action");
        }
        else {
            RolesManager rolesManger = StepperUtils.getRolesManger(getServletContext());
            String roleName = req.getParameter(ServletUtils.ROLE_NAME_PARAMETER);
            if (roleName == null) {
                List<RoleImpl> roles = new ArrayList<>(rolesManger.getRoleMap().values());
                ServletUtils.sendResponse(roles, roles.getClass(), resp);
            } else {
                RoleImpl role = rolesManger.getRoleMap().get(roleName);
                ServletUtils.sendResponse(role, role.getClass(), resp);
            }
        }
    }
}
