package DataPresenter.impl;

import DTO.FlowExecutionData.IOData;
import DataPresenter.api.DataPresenterAbstractClass;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class RelationStringPresenter extends DataPresenterAbstractClass {

    /***
     *Only Files Content Extractor and Files Renamer creates outputs of string relation that have only 3 cols
     */
    public RelationStringPresenter(IOData data){
        super(data);
        List<String> colNames=data.getList();
        List<List<String>> rows = data.getRows();
        List<TableColumn<List<String>, String>> tableColumns = createTableColumns(colNames);
        ObservableList<List<String>> rowData = FXCollections.observableArrayList(rows);

        // Create TableView and set columns and data
        TableView<List<String>> tableView = new TableView<>();
        tableView.getColumns().addAll(tableColumns);
        tableView.setItems(rowData);
        tableView.setMaxWidth(500);
        tableView.setMaxHeight(170);
        presentation.getChildren().add(tableView);
    }

    public static List<TableColumn<List<String>, String>> createTableColumns(List<String> colNames) {
        List<TableColumn<List<String>, String>> tableColumns = FXCollections.observableArrayList();
        Double maxLen=0.0;
        for (int i = 0; i < colNames.size(); i++) {
            final int columnIndex = i;
            TableColumn<List<String>, String> column = new TableColumn<>(colNames.get(i));
            column.setCellValueFactory(cellData -> {
                List<String> rowValues = cellData.getValue();
                String cellValue = rowValues.get(columnIndex);
                return new SimpleStringProperty(cellValue);
            });
            column.setPrefWidth(colNames.get(i).length()*9.5);
            tableColumns.add(column);

        }

        return tableColumns;
    }

}
