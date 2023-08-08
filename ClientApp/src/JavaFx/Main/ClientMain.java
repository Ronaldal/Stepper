package JavaFx.Main;

import JavaFx.ClientUtils;
import Requester.login.LogoutRequestImpl;
import Utils.Utils;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import static JavaFx.ClientUtils.HTTP_CLIENT;


public class ClientMain extends Application {
    private Scene scene;
    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientUtils clientUtils=new ClientUtils();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../Login/login.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Utils.runSync(new LogoutRequestImpl().logoutRequest(), HTTP_CLIENT);
    }
}
