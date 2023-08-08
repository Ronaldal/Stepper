package servlets;

import StepperEngine.Stepper;
import StepperEngine.StepperWithRolesAndUsers;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.StepperUtils;
import utils.Valitator.UserValidator;
import utils.Valitator.UserValidatorImpl;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/flows_names")

public class FlowNamesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserValidator userValidator = new UserValidatorImpl(req);
        if(userValidator.isLoggedIn()) {
            if(userValidator.isAdmin() || userValidator.isAllFlowRole()) {
                StepperWithRolesAndUsers stepper = StepperUtils.getStepper(getServletContext());
                if (stepper != null) {
                    List<String> flowNames = stepper.getFlowNames();
                    ServletUtils.sendResponse(flowNames, flowNames.getClass(), resp);
                }
            }
        }
    }
}
