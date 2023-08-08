package servlets.execution;

import StepperEngine.Stepper;
import StepperEngine.StepperWithRolesAndUsers;
import exceptions.MissingParamException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.roles.RoleDefinition;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.StepperUtils;
import utils.Valitator.UserValidator;
import utils.Valitator.UserValidatorImpl;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@WebServlet(name = "continuation", urlPatterns = "/execution/continue")
public class ContinuationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StepperWithRolesAndUsers stepper = StepperUtils.getStepper(getServletContext());
        UserValidator userValidator = new UserValidatorImpl(req);
        if(userValidator.isLoggedIn()) {
            try {
                Map<String, String> paramMap = ServletUtils.getParamMap(req, ServletUtils.UUID_PARAMETER, ServletUtils.FLOW_NAME_PARAMETER);
                String uuid = paramMap.get(ServletUtils.UUID_PARAMETER);
                String flowToContinue = paramMap.get(ServletUtils.FLOW_NAME_PARAMETER);
                String username = SessionUtils.getUsername(req);
                RoleDefinition[] userRolesAsArray = StepperUtils.getUserRolesAsArray(getServletContext(), username);
                String newExecution = stepper.applyContinuation(
                        flowToContinue,uuid,username,StepperUtils.isManager(getServletContext(),username), userRolesAsArray);

                if (!userValidator.isFlowAllowed(flowToContinue)) {
                    ServletUtils.sendBadRequest(resp, String.format("user cannot execute flow %s", flowToContinue));
                }
                if (newExecution != null) {
                    ServletUtils.sendResponse(newExecution, newExecution.getClass(), resp);
                } else {
                    ServletUtils.sendBadRequest(resp, "No such execution " + uuid);
                }
            } catch (MissingParamException e) {
                ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(e.getMissingParamName()));
            }
        }
        else{
            ServletUtils.sendNotLoggedInBadRequest(resp);
        }
    }

}
