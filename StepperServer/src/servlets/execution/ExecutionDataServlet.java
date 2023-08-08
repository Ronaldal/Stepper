package servlets.execution;

import DTO.FlowExecutionData.FlowExecutionData;
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
import java.util.List;

import static utils.ServletUtils.*;


@WebServlet(name = "executionDataServlet", urlPatterns = "/execution/data")
public class ExecutionDataServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StepperWithRolesAndUsers stepper = StepperUtils.getStepper(getServletContext());
        String uuid = req.getParameter(UUID_PARAMETER);
        String flow_name = req.getParameter(FLOW_NAME_PARAMETER);
        String username = SessionUtils.getUsername(req);
        UserValidator userValidator = new UserValidatorImpl(req);
        RoleDefinition[] userRolesAsArray=null;

        if (userValidator.isLoggedIn()) {
            if(!userValidator.isAdmin())
                userRolesAsArray= StepperUtils.getUserRolesAsArray(getServletContext(), username);
           if (uuid != null) { // the request is for specific execution
                if (userValidator.isExecutionAllowed(uuid)) {
                    FlowExecutionData executionData = stepper.getFlowExecutionData(uuid);
                    ServletUtils.sendResponse(executionData, executionData.getClass(), resp);
                } else {
                    ServletUtils.sendBadRequest(resp, String.format("user %s cannot see execution %s", username, uuid));
                }
            } else if (flow_name != null) { // the request is for a certain flow's executions
                List<FlowExecutionData> flowExecutionDataList = stepper.getFlowExecutionDataMap().get(flow_name);
                stepper.getFlowExecutionsData(flow_name);
                ServletUtils.sendResponse(flowExecutionDataList, flowExecutionDataList.getClass(), resp);
            } else { // the request is for all executions
                List<FlowExecutionData> flowExecutionDataList = null;

                if(userValidator.isAdmin() || userValidator.isManager()) {
                    flowExecutionDataList = stepper.getFlowExecutionDataList();
                }
                else{
                    flowExecutionDataList = stepper.getFlowExecutionDataList(username,userRolesAsArray);
                }
                ServletUtils.sendResponse(flowExecutionDataList, flowExecutionDataList.getClass(), resp);
            }
        }
        else{
            ServletUtils.sendNotLoggedInBadRequest(resp);
        }
    }

}
