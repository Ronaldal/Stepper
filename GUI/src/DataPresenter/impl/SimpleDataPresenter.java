package DataPresenter.impl;

import DTO.FlowExecutionData.IOData;
import DataPresenter.api.DataPresenterAbstractClass;

import javafx.scene.control.Label;


public class SimpleDataPresenter extends DataPresenterAbstractClass {

    public SimpleDataPresenter(IOData data){
        super(data);
        presentation.getChildren().add(new Label(data.getContent()));
    }
}
