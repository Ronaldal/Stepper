package servlets.File;

import Managers.UserDataManager;
import StepperEngine.Flow.FlowBuildExceptions.FlowBuildException;
import StepperEngine.Stepper;
import StepperEngine.StepperReader.Exception.ReaderException;
import StepperEngine.StepperWithRolesAndUsers;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import users.UserManager;
import users.roles.RoleImpl;
import users.roles.RolesManager;
import utils.ServletUtils;
import utils.StepperUtils;
import utils.Valitator.UserValidator;
import utils.Valitator.UserValidatorImpl;

import java.io.*;
import java.util.*;


@WebServlet("/upload-file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileUploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserValidator userValidator = new UserValidatorImpl(request);
        if(userValidator.isAdmin()) {
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            Collection<Part> parts = request.getParts();

            InputStream inputStream = null;
            String filePath = null;
            for (Part part : parts) {
                if (part.getName().equals("file"))
                    inputStream = part.getInputStream();
                else
                    filePath = readFromInputStream(part.getInputStream());
            }
            try {
                StepperWithRolesAndUsers stepper = StepperUtils.getStepper(getServletContext());
                if (!StepperUtils.isStepperIn(getServletContext())) {
                    getServletContext().setAttribute("file-path",filePath);
                    stepper.loadAllStepper(inputStream, filePath);
                    StepperUtils.setStepperIn(getServletContext());
                    StepperUtils.getRolesManger(getServletContext());
                } else {
                    stepper.addFlowsFromFile(inputStream, filePath);
                    updateStatsManager(stepper);
                    updateRoleManager(stepper);
                }


            } catch (ReaderException | FlowBuildException | RuntimeException e) {
                ServletUtils.sendBadRequest(response, e.getMessage());
            }
        }else{
            ServletUtils.sendBadRequest(response, "only admin can upload");
        }

    }

    private void updateRoleManager(StepperWithRolesAndUsers stepper) {
        RolesManager rolesManger = StepperUtils.getRolesManger(getServletContext());
        List<RoleImpl> roles = rolesManger.updateDefaultRoles(stepper);
        roles.forEach(this::updateUsersThatRelatedToRole);
    }

    private void updateUsersThatRelatedToRole(RoleImpl role) {
        UserDataManager userDataManager = ServletUtils.getUserDataManager(getServletContext());
        for(String user: role.getUsers()){
            userDataManager.addRoles(user, role);
        }
    }

    private void updateStatsManager(Stepper stepper) {
        ServletUtils.getStatsManager(getServletContext()).addVersion();
        ServletUtils.getStatsManager(getServletContext()).setFlowExecutionStatsList(stepper.getFlowExecutionStatsList());
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}