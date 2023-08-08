package StepperEngine.DataDefinitions.Relation;

import java.util.ArrayList;
import java.util.List;

public abstract class Relation<T> implements RelationInterface {
    protected List<String> colNames;
    protected  List<T> rows = new ArrayList<>();
    public Relation(){
        this.colNames=new ArrayList<>();
    }

    @Override
    public Integer numOfRows() {
        return rows.size();
    }




}
