package JavaFx.Body.RolesManagement;

import AdminUtils.AdminUtils;
import DTO.FlowDetails.FlowDetails;
import JavaFx.Body.AdminBodyController;
import JavaFx.Body.UserManagement.UserManagement;
import Requester.fileupload.FileUploadImpl;
import Utils.Constants;
import Utils.Utils;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import users.roles.RoleImpl;
import java.util.*;

import static AdminUtils.AdminUtils.HTTP_CLIENT;
import static AdminUtils.AdminUtils.ROLE_REQUEST;

public class RolesManagement {
    @FXML private TableView<RoleImpl> RoleTable;
    @FXML private TableColumn<RoleImpl, String> roleNameCol;
    @FXML private TableColumn<RoleImpl, String> roleDescriptionCol;
    @FXML private Button newRoleButton;
    @FXML private VBox rolePresnterVbox;
    @FXML private ImageView saveButton;
    @FXML private Label roleName;
    @FXML private Label roleDiscription;
    @FXML private ListView<String> flowsListView;
    @FXML private ListView<String> usersListView;
    @FXML private Label editRoleLabel;
    @FXML private Label invalidRoleName;
    @FXML private TextField roleNameTextFiled;
    @FXML private TextField changeDescriptionTextFiled;
    @FXML private ListView<String> flowsEditListView;
    @FXML private ImageView removeFlowImage;
    @FXML private ListView<String> flowsAddListView;
    @FXML private ImageView addFlowImage;
    @FXML private Button removeRoleButton;


    private BooleanProperty flowsEditListViewProperty=new SimpleBooleanProperty();
    private BooleanProperty flowsAddListViewProperty=new SimpleBooleanProperty();
    private Boolean canRoleBeChanged=true;
    private List<RoleImpl> roles=new ArrayList<>();
    private AdminBodyController adminBodyController;
    private RoleImpl newRole;
    private RoleImpl oldRole;
    public static final String READ_ONLY_FLOWS = "Read Only Flows";
    public static final String ALL_FLOWS_ROLE = "All flows";

    @FXML
    void initialize(){
        initRoleColumns();
        disappearSaveButton();
        disappearNewRoleButton();
        setFlowsEditListView();
        setFlowsAddListView();
        FileUploadImpl fileUpload = new FileUploadImpl();

        if(Utils.runSync(fileUpload.isStepperIn(), Boolean.class, HTTP_CLIENT))
        {
            List<RoleImpl> roles= AdminUtils.getRoles(ROLE_REQUEST.getAllRoles(),HTTP_CLIENT);
            setRoleTable(roles);
        }
    }

    @FXML
    void removeRoleAction(ActionEvent event) {
        if(oldRole!= null){
            if(usersListView.getItems().isEmpty() && canRoleBeChanged)
                removeRole();
            else
                makeAlret();
        }

    }

    private void makeAlret() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        if(canRoleBeChanged)
            alert.setContentText("First need to remove all users that related to role!");
        else
            alert.setContentText("This is a default role!\nCann't be removed!");
        alert.showAndWait();
    }

    private void removeRole() {
        cleanUpScreen();
        adminBodyController.removeRole(oldRole);
        RoleTable.getItems().remove(oldRole);
    }

    private void cleanUpScreen() {
        disappearSaveButton();
        roleName.setText("");
        roleDiscription.setText("");
        unableTextFields();
        updateRoleNameInChangeSection("");
        updateRoleDescriptionInChangeSection("");
        canRoleBeChanged=false;
        flowsListView.getItems().clear();
        flowsEditListView.getItems().clear();
        flowsAddListView.getItems().clear();
    }

    private void unableTextFields() {
        roleNameTextFiled.setEditable(true);
        changeDescriptionTextFiled.setEditable(true);
    }

    private void disappearNewRoleButton() {
        newRoleButton.opacityProperty().set(0.2);
        newRoleButton.cursorProperty().set(Cursor.DISAPPEAR);
        newRoleButton.setMouseTransparent(true);
    }
    public void appearNewRoleButton() {
        newRoleButton.opacityProperty().set(1);
        newRoleButton.cursorProperty().set(Cursor.HAND);
        newRoleButton.setMouseTransparent(false);
    }

    private void initRoleColumns() {
        roleNameCol.setCellValueFactory(new PropertyValueFactory<RoleImpl, String>("name"));
        roleDescriptionCol.setCellValueFactory(new PropertyValueFactory<RoleImpl, String>("description"));
    }

    @FXML
    void newRole(ActionEvent event) {
        updateScreenToNewRole();
    }

    private void updateUserTable(String userName, String roleName,boolean add) {
        if(oldRole != null && oldRole.getName().equals(roleName)){
            Platform.runLater(()-> {
                if (add)
                    usersListView.getItems().add(userName);
                else
                    usersListView.getItems().remove(userName);
            });
        }
    }

    public void addOrRemoveUserFromRole(String userName, String roleName,boolean add) {
        RoleImpl newR = null;
        for(RoleImpl role:RoleTable.getItems()){
            if(role.getName().equals(roleName)) {
                newR=new RoleImpl(role);
                if(add)
                    newR.addUser(userName);
                else
                    newR.removeUser(userName);
                adminBodyController.changeRole(newR,role);
                RoleTable.getItems().remove(role);
                RoleTable.getItems().add(newR);
                break;
            }
        }
        updateUserTable(userName, roleName,add);
    }

    private void updateScreenToNewRole() {
        disappearSaveButton();
        unableTextFields();
        oldRole=null;
        updateNewRoleDetails();
        canRoleBeChanged=true;
        newRole=new RoleImpl();
        updateRoleNameInChangeSection("");
        updateRoleDescriptionInChangeSection("");
        updateListsInChangeRoleSection(newRole.getAllowedFlows(),newRole.getUsers());
        editRoleLabel.setText("Add role (to save the role you have to add name!)");
    }

    public void setRoleTable(List<RoleImpl> roleList){
        roles=roleList;
        RoleTable.setItems(FXCollections.observableList(roles));
    }

    public void setMainController(AdminBodyController adminBodyController) {
        this.adminBodyController = adminBodyController;
    }
    @FXML
    void saveRole(MouseEvent event) {
//        newRole.setFlows(new ArrayList<>(flowsEditListView.getItems()));
        if(oldRole!= null){
            if(!Objects.equals(newRole.getName(), oldRole.getName()))
                adminBodyController.changeRolesInUser(newRole.getName(),oldRole.getName());
            adminBodyController.changeRole(newRole,oldRole);
            RoleTable.getItems().remove(oldRole);
        }
        else {
            adminBodyController.sendNewRole(newRole);
        }
        addNewRoleToTable();
        updateScreen();
    }

    private void addNewRoleToTable() {
        RoleTable.getItems().add(newRole);
        oldRole=new RoleImpl(newRole);
        newRole=new RoleImpl(newRole);
    }

    @FXML
    void showRole(MouseEvent event) {
        if(event.getClickCount()==2){
            unableTextFields();
            oldRole=new RoleImpl(RoleTable.getSelectionModel().getSelectedItem());
            newRole=new RoleImpl(oldRole);
            updateScreen();
        }
    }

    private void updateScreen() {
        disappearSaveButton();
        updateRoleDetails();
        editRoleLabel.setText("Change selected role");
        checkIfCanBeChanged(oldRole);
        updateRoleNameInChangeSection(oldRole.getName());
        updateRoleDescriptionInChangeSection(oldRole.getDescription());
        updateListsInChangeRoleSection(newRole.getAllowedFlows(),newRole.getUsers());
    }

    private void updateRoleNameInChangeSection(String name) {
        roleNameTextFiled.setText(name);
        roleNameTextFiled.setEditable(canRoleBeChanged);
    }
    @FXML
    void changeRoleName(ActionEvent event) {
        if (oldRole == null || !oldRole.getName().equals(roleNameTextFiled.getText())) {
            if (checkIfRoleExsitsByName(roleNameTextFiled.getText())) {
                invalidRoleName.textProperty().setValue("invalid input");
                FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), invalidRoleName);
                fadeTransition.setFromValue(1.0);
                fadeTransition.setToValue(0.0);
                fadeTransition.setCycleCount(5);
                fadeTransition.play();
                disappearSaveButton();
            } else {
                invalidRoleName.textProperty().setValue("");
                showSaveButton();
                newRole.setName(roleNameTextFiled.getText());
            }
        }

    }
    private void updateRoleDescriptionInChangeSection(String description) {
        changeDescriptionTextFiled.setPrefWidth(description.length()*9.0);
        changeDescriptionTextFiled.setText(description);
        changeDescriptionTextFiled.setEditable(canRoleBeChanged);
    }


    @FXML
    void changeDescription(ActionEvent event) {
        if(oldRole!=null) {
            showSaveButton();
        }
        newRole.setDescription(changeDescriptionTextFiled.getText());
    }

    private void updateListsInChangeRoleSection(List<String> flowsInRole, Collection<String> users) {
        flowsEditListView.setItems(FXCollections.observableList(flowsInRole));
        flowsAddListView.setItems(getFlowsThatNotInRole(flowsInRole));
    }


    private void updateRoleDetails() {
        roleName.setText(oldRole.getName());
        roleDiscription.setPrefWidth(oldRole.getDescription().length()*9.0);
        roleDiscription.setText(oldRole.getDescription());
        flowsListView.setItems(FXCollections.observableList(oldRole.getAllowedFlows()));
        usersListView.setItems(FXCollections.observableArrayList(oldRole.getUsers()));
    }
    private void updateNewRoleDetails() {
        roleName.setText("New role name");
        roleDiscription.setText("New role Description");
        if(flowsListView.getItems()!=null){
            flowsListView.getItems().clear();
        }
        if(usersListView.getItems()!=null){
            flowsListView.getItems().clear();
        }
    }


    private void checkIfCanBeChanged(RoleImpl oldRole) {
        canRoleBeChanged = !(oldRole.getName().equals("Read Only Flows") || oldRole.getName().equals("All flows"));
    }


    private boolean checkIfRoleExsitsByName(String newName){
        for(RoleImpl role:roles){
            if(role.getName().equals(newName))
                return true;
        }
        return false;
    }

    private void disappearSaveButton() {
        saveButton.opacityProperty().set(0.2);
        saveButton.cursorProperty().set(Cursor.DISAPPEAR);
        saveButton.setMouseTransparent(true);
    }
    private void showSaveButton() {
        saveButton.opacityProperty().set(1);
        saveButton.cursorProperty().set(Cursor.HAND);
        saveButton.setMouseTransparent(false);
    }
    @FXML
    void removeFlow(MouseEvent event) {
        generateListView(flowsEditListView, flowsAddListView);
    }

    private void generateListView(ListView<String> removeFromeListView, ListView<String> addToListView) {
        if(canRoleBeChanged) {
            swapElementBetweenLists(removeFromeListView, addToListView);
        }
    }

    private void swapElementBetweenLists(ListView<String> removeFromeListView, ListView<String> addToListView) {
        if (removeFromeListView.getSelectionModel().getSelectedItem() != null) {
            String flowSelected= removeFromeListView.getSelectionModel().getSelectedItem();
            removeFromeListView.getItems().remove(flowSelected);
            addToListView.getItems().add(flowSelected);
            removeFromeListView.getSelectionModel().clearSelection();
            if(oldRole!=null)
                showSaveButton();
        }
    }

    @FXML
    void addFlow(MouseEvent event) {
        generateListView(flowsAddListView, flowsEditListView);
    }
    private ObservableList<String> getFlowsThatNotInRole(List<String>flowsInRole) {
        List<String> allFlows=adminBodyController.getAllFlows();
        List<String> flowsNotInRole=new ArrayList<>();
        for (String flow:allFlows){
            if (!flowsInRole.contains(flow)){
                flowsNotInRole.add(flow);
            }
        }
        return FXCollections.observableList(flowsNotInRole);
    }

    private void setFlowsAddListView() {
        UserManagement.setProperty(flowsAddListView, flowsAddListViewProperty, addFlowImage);
    }

    private void setFlowsEditListView() {
        UserManagement.setProperty(flowsEditListView, flowsEditListViewProperty, removeFlowImage);
    }


}