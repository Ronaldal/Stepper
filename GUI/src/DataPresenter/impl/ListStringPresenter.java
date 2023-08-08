package DataPresenter.impl;

import DTO.FlowExecutionData.IOData;
import DataPresenter.api.DataPresenterAbstractClass;

import StepperEngine.DataDefinitions.List.StringListDataDef;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.List;

public class ListStringPresenter extends DataPresenterAbstractClass {

    public ListStringPresenter(IOData data){
        super(data);
        StringListDataDef stringListDataDef=data.getDataValue(StringListDataDef.class);
        List<String> strings=stringListDataDef.getStringList();
        GridPane gridPane =new GridPane();
        gridPane.setPadding(new Insets(5));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        for (int i = 0; i < strings.size(); i++) {
            String str = strings.get(i);
            Text text = new Text(str);
            Integer num=i+1;
            Text numText = new Text(num.toString()+".");
            gridPane.add(text, 1, i);
            gridPane.add(numText, 0, i);
        }
        presentation.getChildren().add(gridPane);
    }
}
