package servlets.execution;

import StepperEngine.Flow.execute.FlowExecution;
import StepperEngine.StepperWithRolesAndUsers;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.StepperUtils;

import java.io.IOException;
import java.util.Map;

import static utils.ServletUtils.UUID_PARAMETER;
import static utils.ServletUtils.sendResponse;

@WebServlet(name = "gerFreeInputs", urlPatterns = "/execution/continue/freeInputs")
public class GetFreeInputsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StepperWithRolesAndUsers stepper = StepperUtils.getStepper(getServletContext());
        String uuid = req.getParameter(UUID_PARAMETER);
        if (uuid!=null) {
            FlowExecution flowExecutionByUuid = stepper.getFlowExecutionByUuid(uuid);
            Map<String, Object> freeInputsValue = flowExecutionByUuid.getFreeInputsValue();
            sendResponse(freeInputsValue,freeInputsValue.getClass(),resp);
        }
        else{
            ServletUtils.sendBadRequest(resp,ServletUtils.getMissingParameterMessage(UUID_PARAMETER));
        }
    }
}
