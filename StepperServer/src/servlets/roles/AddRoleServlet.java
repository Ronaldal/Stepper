package servlets.roles;
import Managers.UserDataManager;
import users.UserManager;

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

@WebServlet(name = "addRole", urlPatterns = "/role/addRole")
public class AddRoleServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserValidator userValidator = new UserValidatorImpl(req);
        if(!userValidator.isAdmin()){
            ServletUtils.sendBadRequest(resp, "Only admin has privilege for this action");
        }
        else {
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            Gson gson = new Gson();
            CombinedRoles combinedRoles=gson.fromJson(json,CombinedRoles.class);
            RoleImpl newRole = combinedRoles.getNewRole();
            RoleImpl oldRole = combinedRoles.getOldRole();
            if (newRole != null) {
                RolesManager rolesManager = StepperUtils.getRolesManger(getServletContext());
                if(oldRole==null) {
                    rolesManager.addRole(newRole);
                }else{
                    rolesManager.changeRoles(newRole,oldRole);
                    removeRoleToUser(oldRole);
                }
                addRoleToUser(newRole);
                ServletUtils.sendResponse(true, Boolean.class, resp);
            }
            else {
                ServletUtils.sendBadRequest(resp,"No new role sended!!");
            }
        }
    }

    private void removeRoleToUser(RoleImpl oldRole) {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        UserDataManager userDataManager = ServletUtils.getUserDataManager(getServletContext());
        for (String name:oldRole.getUsers()){
            userManager.removeRoleFromUser(name,oldRole);
            userDataManager.removeRoles(name,oldRole,userManager.getUserRoles(name));
        }
    }

    private void addRoleToUser(RoleImpl newRole) {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        UserDataManager userDataManager = ServletUtils.getUserDataManager(getServletContext());
        for(String name: newRole.getUsers()){
            userManager.addRoleToUser(name, newRole);
            userDataManager.addRoles(name,newRole);
        }
    }

    public class CombinedRoles {
        private RoleImpl newRole;
        private RoleImpl oldRole;

        public RoleImpl getNewRole() {
            return newRole;
        }

        public RoleImpl getOldRole() {
            return oldRole;
        }
    }
}