package servlets.execution;

import StepperEngine.Stepper;
import StepperEngine.StepperWithRolesAndUsers;
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
@WebServlet(name="rerunFlow", urlPatterns = "/execution/rerun")
public class RerunServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StepperWithRolesAndUsers stepper = StepperUtils.getStepper(getServletContext());
        String uuid = req.getParameter(ServletUtils.UUID_PARAMETER);
        String username = SessionUtils.getUsername(req);
        UserValidator userValidator = new UserValidatorImpl(req);
        if(userValidator.isLoggedIn()) {
            if (uuid != null) {
                String newExecution = stepper.reRunFlow(uuid);
                if (newExecution != null) {
                    if(userValidator.isExecutionAllowed(newExecution)) {
                        ServletUtils.sendResponse(newExecution, newExecution.getClass(), resp);
                    }else{
                        ServletUtils.sendExecutionNotAllowedBadRequest(resp, username, newExecution);
                    }
                } else {
                    ServletUtils.sendBadRequest(resp, "No such execution " + uuid);
                }
            } else {
                ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(ServletUtils.UUID_PARAMETER));
            }
        }
    }
}
