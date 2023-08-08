package servlets.execution;

import Managers.UserDataManager;
import Managers.UserDataManager;
import StepperEngine.Flow.execute.ExecutionNotReadyException;
import StepperEngine.Stepper;
import StepperEngine.StepperWithRolesAndUsers;
import exceptions.MissingParamException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UserManager;
import utils.ServletUtils;
import Managers.StatsManager;
import utils.SessionUtils;
import utils.StepperUtils;
import utils.Valitator.UserValidator;
import utils.Valitator.UserValidatorImpl;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "executionServlet", urlPatterns = "/execution")
public class ExecutionServlet extends HttpServlet {
    private static final String UUID_PARAMETER = "uuid";
    private static final String FREE_INPUT_PARAMETER = "free-input";
    private static final String FREE_INPUT_DATA_PARAMETER = "data";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // gets an uuid of a new execution created by the engine.
        // must pass a flow name

        StepperWithRolesAndUsers stepper = StepperUtils.getStepper(getServletContext());
        UserValidator userValidator = new UserValidatorImpl(req);
        String username = SessionUtils.getUsername(req);
        String flowName = req.getParameter(ServletUtils.FLOW_NAME_PARAMETER);
        if(userValidator.isLoggedIn()) {
            if (flowName != null) {
                if(userValidator.isFlowAllowed(flowName)) {
                    String newExecution = stepper.createNewExecution(flowName, username,StepperUtils.isManager(getServletContext(),username));
                    ServletUtils.sendResponse(newExecution, newExecution.getClass(), resp);
                }else{
                    ServletUtils.sendFlowNotAllowedBadRequest(resp, username, flowName);
                }
            } else {
                ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(ServletUtils.FLOW_NAME_PARAMETER));
            }
        }else{
            ServletUtils.sendNotLoggedInBadRequest(resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // execute a flow. gets a parameter the UUID of the execution.
        StatsManager statsManager = ServletUtils.getStatsManager(getServletContext());
        StepperWithRolesAndUsers stepper = StepperUtils.getStepper(getServletContext());
        String uuid = req.getParameter(UUID_PARAMETER);
        UserValidator userValidator = new UserValidatorImpl(req);
        String username = SessionUtils.getUsername(req);
        if(userValidator.isLoggedIn()) {
            if (uuid != null) {
                if(userValidator.isExecutionAllowed(uuid)) {
                    try {
                        stepper.executeFlow(uuid);
                        while (!stepper.isExecutionEnded(uuid)) {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        synchronized (getServletContext()) {
                            statsManager.setFlowExecutionStatsList(stepper.getFlowExecutionStatsList());
                            statsManager.addVersion();
                            UserDataManager userDataManager = ServletUtils.getUserDataManager(getServletContext());
                            userDataManager.addExecutionToUser(SessionUtils.getUsername(req));
                        }
                        ServletUtils.sendResponse(Boolean.TRUE, Boolean.class, resp);
                    } catch (ExecutionNotReadyException e) {
                        ServletUtils.sendBadRequest(resp, e.getMessage());
                    }
                }else{
                    ServletUtils.sendExecutionNotAllowedBadRequest(resp, username, uuid);
                }
            } else {
                ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(UUID_PARAMETER));
            }
        }else{
            ServletUtils.sendNotLoggedInBadRequest(resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StepperWithRolesAndUsers stepper = StepperUtils.getStepper(getServletContext());
        Map<String, String> paramMap = null;
        UserValidator userValidator = new UserValidatorImpl(req);
        String username = SessionUtils.getUsername(req);
        if(userValidator.isLoggedIn()) {
            try {
                paramMap = ServletUtils.getParamMap(req, UUID_PARAMETER, FREE_INPUT_PARAMETER, FREE_INPUT_DATA_PARAMETER);
                String uuid = paramMap.get(UUID_PARAMETER);
                String freeInputName = paramMap.get(FREE_INPUT_PARAMETER);
                String dataParam = paramMap.get(FREE_INPUT_DATA_PARAMETER);
                Boolean result = null;
                try {
                    if(userValidator.isExecutionAllowed(uuid)) {
                        result = stepper.addFreeInputToExecution(uuid, freeInputName, Integer.valueOf(dataParam));
                    }else{
                        ServletUtils.sendExecutionNotAllowedBadRequest(resp, username, uuid);
                    }
                } catch (NumberFormatException e) {
                    try {
                        result = stepper.addFreeInputToExecution(uuid, freeInputName, Double.valueOf(dataParam));
                    } catch (NumberFormatException e1) {
                        result = stepper.addFreeInputToExecution(uuid, freeInputName, dataParam);
                    }
                }
                ServletUtils.sendResponse(result, result.getClass(), resp);
            } catch (MissingParamException e) {
                ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(e.getMissingParamName()));
            }
        }else{
            ServletUtils.sendNotLoggedInBadRequest(resp);
        }
    }
}
