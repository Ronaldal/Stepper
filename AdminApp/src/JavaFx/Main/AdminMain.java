package JavaFx.Main;

import Requester.login.LogoutRequestImpl;
import Utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import okhttp3.HttpUrl;
import okhttp3.Request;


import static AdminUtils.AdminUtils.HTTP_CLIENT;
import static Utils.Constants.ADMIN_LOGIN_PAGE;
import static Utils.Constants.BASE_URL;


public class AdminMain extends Application {
    private Scene scene;
    @Override
    public void start(Stage primaryStage) throws Exception {
        boolean isAdminLoggedIn = signInAsAdmin();
        if(isAdminLoggedIn) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AdminMain.fxml"));
            Parent root = fxmlLoader.load();
            scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Admin login error");
            alert.setHeaderText(null);
            alert.setContentText("ADMIN IS ALREADY LOGGED IN");
            alert.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private boolean signInAsAdmin(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + ADMIN_LOGIN_PAGE).newBuilder();
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Boolean result = Utils.runSync(request, Boolean.class, HTTP_CLIENT);
        return result;
    }

    @Override
    public void stop() throws Exception {
        Utils.runSync(new LogoutRequestImpl().logoutRequest(), HTTP_CLIENT);
        super.stop();
    }
}
