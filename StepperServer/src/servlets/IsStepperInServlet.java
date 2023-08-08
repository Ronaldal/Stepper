package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.StepperUtils;

import java.io.IOException;


@WebServlet(name = "isStepperIn", urlPatterns = "/isStepperIn")
public class IsStepperInServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean stepperIn = StepperUtils.isStepperIn(getServletContext());
        ServletUtils.sendResponse(stepperIn,Boolean.class,resp);
    }
}
