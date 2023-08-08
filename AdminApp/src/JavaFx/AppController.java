package JavaFx;

import AdminUtils.AdminUtils;
import DTO.ExecutionsStatistics.FlowExecutionStats;
import JavaFx.Body.AdminBodyController;
import JavaFx.Header.HeaderController;
import Requester.fileupload.FileUploadImpl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.IOException;
import java.util.List;

import Utils.Utils;
import okhttp3.HttpUrl;
import okhttp3.Request;
import users.roles.RoleImpl;

import static AdminUtils.AdminUtils.*;
import static Utils.Constants.*;

public class AppController {
    @FXML private VBox headerComponent;
    @FXML private TabPane bodyComponent;
    @FXML HeaderController headerComponentController;
    @FXML AdminBodyController bodyComponentController;
    boolean isStepperIn=false;

    @FXML
    public void initialize() {
        //signInAsAdmin();
        headerComponentController.setMainController(this);
        bodyComponentController.setMainController(this);
        FileUploadImpl fileUpload = new FileUploadImpl();
        isStepperIn=Utils.runSync(fileUpload.isStepperIn(), Boolean.class, HTTP_CLIENT);
    }


    private void signInAsAdmin(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + ADMIN_LOGIN_PAGE).newBuilder();
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Utils.runSync(request, HTTP_CLIENT);
    }

    public void loadFile(String filePath,File selectedFile) throws IOException {

        try {
            String res=Utils.runSyncFile(FILE_UPLOAD.fileUploadRequest(selectedFile), AdminUtils.HTTP_CLIENT);
            if(res!=null) {
                System.out.println(res);
                failureMessage(res);
            }
            else if(!isStepperIn) {
                isStepperIn = true;
                Platform.runLater(() -> {
                    headerComponentController.updateFilePathLabel(filePath);
                    updateStats();
                    updateRoles();
                });
            }
            else {
                Platform.runLater(() -> {
                    updateStats();
                    updateRoles();
                });
            }
        }catch (RuntimeException e) {
            System.out.println(e.getMessage());
            failureMessage(e.getMessage());
        }
    }

    private void updateRoles() {
        List<RoleImpl> roles=AdminUtils.getRoles(ROLE_REQUEST.getAllRoles(),HTTP_CLIENT);
        bodyComponentController.setRoles(roles);
    }

    private void updateStats()  {
        bodyComponentController.initStats(Utils.runSync(FLOWS_NAMES_REQUEST.getAllFlowNamesRequest(),
                STRING_LIST_INSTANCE.getClass(),AdminUtils.HTTP_CLIENT));
    }

    private static void failureMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Stepper");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean isStepperIn() {
        return isStepperIn;
    }

    public FlowExecutionStats getFlowExecutionsStats(String flowName) {
        return Utils.runSync(FLOW_STATS_REQUEST.getFlowRequest(flowName),
                FlowExecutionStats.class,AdminUtils.HTTP_CLIENT);
    }

}
