package StepperEngine.DataDefinitions.Relation;

import java.util.List;
import java.util.stream.Collectors;

public class RelationOfStringRows extends Relation<List<String>> {

    public RelationOfStringRows(List<String> colNames){
        this.colNames=colNames;
    }
    public void addRow(List<String> rowToAdd){
        rows.add(rowToAdd);
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    /***
     * A CSV file is a text file where the first line contains names of columns separated by "," and then each line contains a collection of values separated by "," according to the number of columns.
     * @return CSV file
     */
    @Override
    public String relationToCSV(){
        String CSV= String.join(",", colNames);
        for(List<String> row:rows){
            CSV=CSV+"\n"+String.join(", ", row);
        }
        return CSV;
    }

    /***
     * The properties file is a text file with the structure of key=value.
     * For each row i and column j we will define the Key in the following structure: Row-i.<column-j-name> and the value will actually be the cell value of row i and column j.
     * @param totalProperties
     * @return String of properties file
     */
    @Override
    public String createPropertiesExporter(Integer totalProperties) {
        String propertiesExporter="";
        Integer num=0;
        totalProperties=0;
        for(List<String> row:rows){
            num++;
            propertiesExporter+= "row-"+num.toString()+".";
            for(int currIndexRow=0,currIndexCol=0;currIndexRow<colNames.size()-1;currIndexRow++,currIndexCol++){
                totalProperties++;
                propertiesExporter+= colNames.get(currIndexCol)+"="+row.get(currIndexRow)+" | ";
            }
            propertiesExporter+= colNames.get(colNames.size()-1)+"="+row.get(colNames.size()-1);
            propertiesExporter+=".\n";
        }
        return propertiesExporter;
    }

    @Override
    public String toString() {
        return String.join(" | ", colNames)+"\n"+
                rows.stream()
                        .map(row -> String.join(" | ", row))
                        .collect(Collectors.joining("\n"));
    }

    public List<String> getColNames(){
        return colNames;
    }
    public List<List<String>> getRows(){
        return rows;
    }

}
