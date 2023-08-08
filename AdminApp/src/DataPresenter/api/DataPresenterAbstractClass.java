package DataPresenter.api;



import DTO.FlowExecutionData.IOData;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public abstract class DataPresenterAbstractClass implements DataPresenter {
    protected HBox hBox;
    protected VBox presentation;
    @Override
    public Node getPresenter() {

        return presentation;
    }
    protected DataPresenterAbstractClass(IOData data){
        hBox=new HBox();
        presentation = new VBox();
        presentation.getStyleClass().add("framed-vbox");
        presentation.setSpacing(5);
        presentation.alignmentProperty().set(Pos.TOP_CENTER);
        Label nameLabel=new Label(data.getName());
        nameLabel.getStyleClass().add("label-flowName");
        presentation.getChildren().add(nameLabel);
        presentation.getChildren().add(new Label(data.getType()));
        Label userStringLabel=new Label(data.getUserString()+":");
        userStringLabel.getStyleClass().add("label-userString");
        presentation.getChildren().add(userStringLabel);
    }
}
