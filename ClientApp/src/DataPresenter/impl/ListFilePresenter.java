package DataPresenter.impl;

import DTO.FlowExecutionData.IOData;
import DataPresenter.api.DataPresenterAbstractClass;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.File;
import java.util.List;

public class ListFilePresenter extends DataPresenterAbstractClass {
    public ListFilePresenter(IOData data){
        super(data);
        List<String> filePaths = data.getList();
        GridPane gridPane =new GridPane();
        gridPane.setPadding(new Insets(5));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        for(int i=0;i<filePaths.size();i++){
            Text text = new Text(filePaths.get(i));
            Integer num=i+1;
            Text numText = new Text(num.toString()+".");
            gridPane.add(text, 1, i);
            gridPane.add(numText, 0, i);
        }
        presentation.getChildren().add(gridPane);
    }
}
