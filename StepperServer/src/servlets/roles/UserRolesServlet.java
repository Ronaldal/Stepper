package servlets.roles;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.roles.RoleDefinition;
import users.roles.RoleImpl;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.StepperUtils;
import utils.Valitator.UserValidator;
import utils.Valitator.UserValidatorImpl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@WebServlet(urlPatterns = "/user/role")
public class UserRolesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserValidator userValidator = new UserValidatorImpl(req);

        if(!userValidator.isLoggedIn()){
            ServletUtils.sendNotLoggedInBadRequest(resp);
        } else if (userValidator.isAdmin()) {
            ServletUtils.sendBadRequest(resp, "Admin has no defined roles");
        } else{
            String username = SessionUtils.getUsername(req);
            Set<RoleDefinition> userRoles = StepperUtils.getUserRoles(getServletContext(), username);
            List<String> roleNames = userRoles.stream()
                    .map(RoleDefinition::getName)
                    .collect(Collectors.toList());
            ServletUtils.sendResponse(roleNames, roleNames.getClass(), resp);
        }
    }
}
