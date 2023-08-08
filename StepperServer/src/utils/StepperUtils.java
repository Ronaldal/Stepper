package utils;


import StepperEngine.Stepper;
import StepperEngine.StepperWithRolesAndUsers;
import jakarta.servlet.ServletContext;
import users.UserManager;
import users.roles.RoleDefinition;
import users.roles.RolesManager;

import java.util.Set;

public class StepperUtils {

    private static final String STEPPER_ATTRIBUTE_NAME = "stepper";
    private static final String STEPPER_LOADED_ATTRIBUTE = "is_stepper_loaded";
    private static final String ROLE_ATTRIBUTE="roles";
    private static final String ROLE_MAP_ATTRIBUTE = "role_map";
    private static final Object stepperLock = new Object();

    public static StepperWithRolesAndUsers getStepper(ServletContext servletContext){
        Stepper stepper;
        synchronized (stepperLock){
            if(servletContext.getAttribute(STEPPER_ATTRIBUTE_NAME) == null){
                stepper = new StepperWithRolesAndUsers();
                servletContext.setAttribute(STEPPER_ATTRIBUTE_NAME,stepper);
            }
        }
        return (StepperWithRolesAndUsers) servletContext.getAttribute(STEPPER_ATTRIBUTE_NAME);
    }
    public static boolean isStepperIn(ServletContext servletContext) {
        synchronized (stepperLock)
        {
            if (servletContext.getAttribute(STEPPER_LOADED_ATTRIBUTE)==null){
                servletContext.setAttribute(STEPPER_LOADED_ATTRIBUTE,false);
            }
        }
        return (Boolean) servletContext.getAttribute(STEPPER_LOADED_ATTRIBUTE);
    }
    public static void setStepperIn(ServletContext servletContext) {
        synchronized (stepperLock)
        {
            servletContext.setAttribute(STEPPER_LOADED_ATTRIBUTE,true);
        }
    }

    public static RolesManager getRolesManger(ServletContext servletContext){
        RolesManager rolesManager;
        synchronized (stepperLock){
            if(servletContext.getAttribute(ROLE_ATTRIBUTE)==null){
                StepperWithRolesAndUsers stepper = getStepper(servletContext);
                rolesManager=new RolesManager(stepper);
                servletContext.setAttribute(ROLE_ATTRIBUTE,rolesManager);
            }
        }
        rolesManager=(RolesManager) servletContext.getAttribute(ROLE_ATTRIBUTE);
        return rolesManager;
    }

    public static Set<RoleDefinition> getUserRoles(ServletContext servletContext, String username){
        UserManager userManager = ServletUtils.getUserManager(servletContext);
        return userManager.getUserRoles(username);
    }

    public static boolean isManager(ServletContext servletContext,String userName){
        UserManager userManager = ServletUtils.getUserManager(servletContext);
        return userManager.isUserManager(userName);
    }

    public static RoleDefinition[] getUserRolesAsArray(ServletContext servletContext, String username){
        Set<RoleDefinition> userRoles = getUserRoles(servletContext, username);
        return userRoles.toArray(new RoleDefinition[0]);
    }

    public static void addDefaultRoles(ServletContext servletContext){
        RolesManager rolesManager=(RolesManager) servletContext.getAttribute(ROLE_ATTRIBUTE);
        rolesManager.addReadOnlyRole(getStepper(servletContext));
        rolesManager.addAllFlowsRole(getStepper(servletContext));
    }

    public static String getFilePath(ServletContext servletContext) {
        if (StepperUtils.isStepperIn(servletContext)){
            return (String) servletContext.getAttribute("file-path");
        }
        return null;
    }
}
