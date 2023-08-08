package StepperEngine.DataDefinitions.List;

import java.util.List;

public class StringListDataDef extends DataDefList<String>{
    public StringListDataDef(List<String> list) {
        this.list=list;
    }
    public List<String> getStringList(){return list;}
    @Override
    public String toString() {
        String res="";
        int i=1;
        for(String str:list){
            res+=i+"."+str;
            if(i!= list.size())
                res+="\n";
        }
        return res;
    }
}
