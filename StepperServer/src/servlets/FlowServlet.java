package servlets;

import DTO.FlowDetails.FlowDetails;
import StepperEngine.Flow.FlowBuildExceptions.FlowBuildException;
import StepperEngine.Stepper;
import StepperEngine.StepperReader.Exception.ReaderException;
import StepperEngine.StepperWithRolesAndUsers;
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
import java.io.InputStream;
import java.util.List;
import java.util.Set;

@WebServlet(name = "getFlowsFromEngine", urlPatterns = {"/flows"})
public class FlowServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserValidator userValidator = new UserValidatorImpl(req);
        String username = SessionUtils.getUsername(req);
        if(userValidator.isLoggedIn()) {
            StepperWithRolesAndUsers stepper = StepperUtils.getStepper(getServletContext());
            String flowName = req.getParameter(ServletUtils.FLOW_NAME_PARAMETER);
            if (flowName != null) {
                if(userValidator.isFlowAllowed(flowName)) {
                    doGetForSpecificFlow(resp, stepper, flowName);
                }
                else{
                    ServletUtils.sendBadRequest(resp, String.format("user cannot access flow %s", flowName));
                }
            } else {
                if(userValidator.isAdmin() || userValidator.isAllFlowRole())
                    doGetForAllFlows(resp, stepper);
                else
                    doGetForAllUserFlows(resp, stepper, username);
            }
        }
        else { // user is not logged in
            ServletUtils.sendBadRequest(resp, ServletUtils.NOT_LOGIN_ERROR_MESSAGE);
        }
    }

    private void doGetForAllFlows(HttpServletResponse resp, StepperWithRolesAndUsers stepper) throws IOException {
        List<FlowDetails> flowDetailsList = stepper.getFlowsDetails();
        ServletUtils.sendResponse(flowDetailsList, flowDetailsList.getClass(), resp);
    }

    private void doGetForSpecificFlow(HttpServletResponse resp, StepperWithRolesAndUsers stepper, String flowName) throws IOException {
        FlowDetails flowDetails = stepper.getFlowsDetailsByName(flowName);
        if(flowDetails == null){
            ServletUtils.sendBadRequest(resp, String.format("flow %s is not exist", flowName));
        }
        else{
            ServletUtils.sendResponse(flowDetails, flowDetails.getClass(), resp);
        }
    }

    private void doGetForAllUserFlows(HttpServletResponse resp, StepperWithRolesAndUsers stepper, String username) throws IOException {
        RoleDefinition[] userRoles = StepperUtils.getUserRolesAsArray(getServletContext(), username);
        List<FlowDetails> flowsDetails = stepper.getFlowsDetails(userRoles);
        ServletUtils.sendResponse(flowsDetails, flowsDetails.getClass(), resp);
    }

}
