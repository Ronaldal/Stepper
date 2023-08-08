package JavaFx.Body.ExecutionData;

import javafx.scene.Node;
import javafx.scene.layout.VBox;


public interface ExecutionData {

 VBox getVbox();
 Node getStepVbox(String stepName);
}
