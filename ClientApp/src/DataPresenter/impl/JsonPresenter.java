package DataPresenter.impl;

import DTO.FlowExecutionData.IOData;
import DataPresenter.api.DataPresenterAbstractClass;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class JsonPresenter extends DataPresenterAbstractClass {

    public JsonPresenter(IOData data){
        super(data);
        JsonElement json=data.getJsonElement();
        TreeItem<String> rootItem = convertJsonObjectToTreeItem(json, "Root");
        TreeView<String> treeView = new TreeView<>(rootItem);
        presentation.getChildren().add(treeView);
    }
    private TreeItem<String> convertJsonObjectToTreeItem(JsonElement jsonElement, String name) {
        TreeItem<String> rootItem = new TreeItem<>(name);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (String key : jsonObject.keySet()) {
                JsonElement value = jsonObject.get(key);
                TreeItem<String> childItem = convertJsonObjectToTreeItem(value, key);
                rootItem.getChildren().add(childItem);
            }
        } else {
            rootItem.setValue(name + ": " + jsonElement.getAsString());
        }
        return rootItem;
    }
}
