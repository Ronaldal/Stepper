package servlets.File;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.StepperUtils;

import java.io.IOException;


@WebServlet(name = "getFile", urlPatterns = "/filePath")
public class getFilePath extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filePath=StepperUtils.getFilePath(getServletContext());
        if(filePath!= null)
            ServletUtils.sendResponse(filePath,String.class,resp);
    }
}
