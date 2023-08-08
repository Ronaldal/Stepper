package servlets.execution;

import StepperEngine.Stepper;
import StepperEngine.StepperWithRolesAndUsers;
import exceptions.MissingParamException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.StepperUtils;
import utils.Valitator.UserValidator;
import utils.Valitator.UserValidatorImpl;

import java.io.IOException;
import java.util.Map;

import static utils.ServletUtils.UUID_PARAMETER;

@WebServlet(name = "executionStatus", urlPatterns = "/execution/status")
public class ExecutionStatusServlet extends HttpServlet {



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StepperWithRolesAndUsers stepper = StepperUtils.getStepper(getServletContext());
        UserValidator userValidator = new UserValidatorImpl(req);
        String username = SessionUtils.getUsername(req);
        if(userValidator.isLoggedIn()) {
            try {
                Map<String, String> paramMap = ServletUtils.getParamMap(req, UUID_PARAMETER);
                String uuid = paramMap.get(UUID_PARAMETER);
                if(userValidator.isExecutionAllowed(uuid)) {
                    Boolean isEnded = stepper.isExecutionEnded(uuid);
                    ServletUtils.sendResponse(isEnded, isEnded.getClass(), resp);
                }
                else{
                    ServletUtils.sendExecutionNotAllowedBadRequest(resp, username, uuid);
                }
            } catch (MissingParamException e) {
                ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(e.getMissingParamName()));
            }
        }else{
            ServletUtils.sendNotLoggedInBadRequest(resp);
        }


    }
}
