package JavaFx.Body;

import DTO.ExecutionsStatistics.FlowExecutionStats;
import DTO.FlowExecutionData.FlowExecutionData;
import AdminUtils.AdminUtils;
import JavaFx.AppController;

import JavaFx.Body.ExecutionData.ExecutionData;
import JavaFx.Body.ExecutionData.FlowExecutionDataImpUI;
import JavaFx.Body.FlowHistory.FlowHistory;
import JavaFx.Body.FlowStats.FlowStats;

import JavaFx.Body.RolesManagement.RolesManagement;
import DTO.UserData;
import JavaFx.Body.UserManagement.UserManagement;
import Refresher.FlowExecutionListRefresher;
import Refresher.Stats.FlowStatsListRefresher;
import Refresher.Stats.StatsWithVersion;
import Refresher.Users.UsersAndVersion;
import Refresher.Users.UsersDataRefresher;
import StepperEngine.Step.api.StepStatus;
import Utils.Utils;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.roles.RoleImpl;

import java.io.IOException;
import java.util.*;

import static AdminUtils.AdminUtils.FLOWS_NAMES_REQUEST;
import static AdminUtils.AdminUtils.USERS_REQUESTER;
import static Utils.Constants.STRING_LIST_INSTANCE;
import static Utils.Constants.STRING_SET_INSTANCE;

public class AdminBodyController {

    @FXML private TabPane bodyComponent;
    @FXML private Tab UserManagementTab;
    @FXML private UserManagement userManagementController;
    @FXML private Tab rolesManagementTab;
    @FXML private RolesManagement rolesManagementController;
    @FXML private Tab flowHistoryTab;
    @FXML private FlowHistory flowHistoryController;
    @FXML private Tab flowStatsTab;
    @FXML private FlowStats flowStatsController;

    private Map<String, ExecutionData> executionDataMap=new HashMap<>();
    private Map<String, Map<String,ExecutionData>> executionStepsInFLow=new HashMap<>();
    private AppController mainController;
    private TimerTask flowExecutionsRefresher;
    private Timer timer;
    private Timer timerStats;
    private Timer usersTimer;
    private UsersDataRefresher usersDataRefresher;
    private FlowStatsListRefresher FlowStatsListRefresher;
    private IntegerProperty statsVersion;
    private Boolean statsRefresherIn=false;
    private Map<String, UserData> userDataMap=new HashMap<>();


    @FXML
    public void initialize(){
        rolesManagementController.setMainController(this);
        userManagementController.setMainController(this);
        flowHistoryController.setMainController(this);
        flowStatsController.setMainController(this);
        statsVersion= new SimpleIntegerProperty();
        flowsHistoryRefresher();
        usersDataMapRefresher();
    }



    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public boolean isStepperIn(){
        return mainController.isStepperIn();
    }

    public void updateFlowHistory() {
        flowHistoryController.setFlowsExecutionTable();
    }

    public void initStats(List<String> flowNames){
        if(!getStatsRefresherIn())
            flowStatsController.initStats(flowNames);
    }

    public ExecutionData getFlowExecutionData(FlowExecutionData flow){
        if(!executionDataMap.containsKey(flow.getUniqueExecutionId()))
            executionDataMap.put(flow.getUniqueExecutionId(),new FlowExecutionDataImpUI(flow));
        return executionDataMap.get(flow.getUniqueExecutionId());
    }
    public Node getStepExecutionData(FlowExecutionData flow, String stepName){
        return executionDataMap.get(flow.getUniqueExecutionId()).getStepVbox(stepName);
    }
    public ImageView getExecutionStatusImage(String status){
        ImageView imageView ;

        switch (StepStatus.valueOf(status)){
            case WARNING:
                imageView=new ImageView("JavaFx/Body/resource/warning.png");
                break;
            case SUCCESS:
                imageView=new ImageView("JavaFx/Body/resource/success.png");
                break;
            default:
                imageView=new ImageView("JavaFx/Body/resource/fail.png");
                break;
        }
        imageView.setFitHeight(15);
        imageView.setFitWidth(15);
        return imageView;

    }

    public FlowExecutionStats getFlowExecutionsStats(String flowName) {
        return mainController.getFlowExecutionsStats(flowName);
    }



    private void setUsersDataMap(UsersAndVersion usersAndVersion) {
        if(!usersAndVersion.getEntries().isEmpty()) {
            usersDataRefresher.setUsersVersion(usersAndVersion.getUserVersion());
            for (UserData userData : usersAndVersion.getEntries()) {
                userDataMap.put(userData.getUserName(), userData);
            }
            Platform.runLater(()->{
                userManagementController.updateNewData(userDataMap.values(),usersAndVersion.getEntries());
            });
        }else if(!usersAndVersion.getDeletedusers().isEmpty()) {
            usersDataRefresher.setUsersVersion(usersAndVersion.getUserVersion());
            for(String userName:usersAndVersion.getDeletedusers()){
                userDataMap.remove(userName);
            }
            Platform.runLater(() -> {

                userManagementController.updateNewData(userDataMap.values(), usersAndVersion.getEntries());
            });
        }


    }
    private void usersDataMapRefresher() {
        usersDataRefresher=new UsersDataRefresher(this::setUsersDataMap);
        usersTimer=new Timer();
        usersTimer.schedule(usersDataRefresher,2000,2000);
    }

    public void flowsHistoryRefresher(){
        flowExecutionsRefresher = new FlowExecutionListRefresher(this::setFlowExecutionDetailsList);
        timer = new Timer();
        timer.schedule(flowExecutionsRefresher, 2000, 2000);
    }
    public void setFlowExecutionDetailsList(List<FlowExecutionData> flowExecutionDataList){
        flowHistoryController.setFlowExecutionDataList(flowExecutionDataList);
    }
    public void updateStats(){
        FlowStatsListRefresher = new FlowStatsListRefresher(
                this::updateStatsWithVersion,
                statsVersion);
        timer = new Timer();
        timer.schedule(FlowStatsListRefresher, 2000, 2000);
    }
    public void updateStatsWithVersion(StatsWithVersion statsWithVersion){
        if((statsWithVersion.getVersion() != statsVersion.get()))
        {
            Platform.runLater(()->{
                setFlowExecutionStatsList(statsWithVersion.getEntries());
                statsVersion.set(statsWithVersion.getVersion());
            });

        }
    }
    public void setFlowExecutionStatsList(List<FlowExecutionStats> flowExecutionStatsList){
        flowStatsController.setFlowExecutionStatsList(flowExecutionStatsList);
    }
    public Boolean getStatsRefresherIn() {
        return statsRefresherIn;
    }

    public void setStatsRefresherIn(Boolean statsRefresherIn) {
        this.statsRefresherIn = statsRefresherIn;
    }
    public void setRoles(List<RoleImpl> roles){
        rolesManagementController.setRoleTable(roles);
        rolesManagementController.appearNewRoleButton();
    }

    public List<String> getAllFlows() {
        return Utils.runSync(FLOWS_NAMES_REQUEST.getAllFlowNamesRequest(),
                STRING_LIST_INSTANCE.getClass(), AdminUtils.HTTP_CLIENT);
    }

    public void changeRole(RoleImpl newRole, RoleImpl oldRole) {
        Utils.runAsync(AdminUtils.ROLE_REQUEST.changeRole(newRole,oldRole),
                setNewRoleCallback,AdminUtils.HTTP_CLIENT );
    }

    public void sendNewRole(RoleImpl newRole) {
        Utils.runAsync(AdminUtils.ROLE_REQUEST.addRole(newRole),
                setNewRoleCallback,AdminUtils.HTTP_CLIENT );
        Platform.runLater(()->userManagementController.addNewRoleToList(newRole.getName()));
    }
    public void addOrRemoveUserFromRole(String userName,String roleName,boolean add){
        rolesManagementController.addOrRemoveUserFromRole(userName,roleName,add);
    }

    public synchronized Set<String> getUsers(){
        return Utils.runSync(USERS_REQUESTER.getUsers(),
              STRING_SET_INSTANCE.getClass(), AdminUtils.HTTP_CLIENT);
    }

    public UserData getUserData(String userName){
        return userDataMap.get(userName);
    }
    public Set<String> getAllUsers(){
        return userDataMap.keySet();
    }

    public void updateUserAsManager(String userName) {
        Utils.runAsync(AdminUtils.USERS_REQUESTER.addManager(userName),setNewRoleCallback,AdminUtils.HTTP_CLIENT);
    }

    public void removeManager(String userName) {
        Utils.runAsync(AdminUtils.USERS_REQUESTER.removeManager(userName),setNewRoleCallback,AdminUtils.HTTP_CLIENT);
    }

    public void removeRole(RoleImpl oldRole) {
        Utils.runAsync(AdminUtils.ROLE_REQUEST.removeRole(oldRole.getName()),removeRoleCallback,AdminUtils.HTTP_CLIENT );
        Platform.runLater(()-> userManagementController.removeRole(oldRole.getName()));
    }

    public void changeRolesInUser(String newRole, String oldRole) {
        userManagementController.changeRoleName(newRole,oldRole);
    }
    public final Callback setNewRoleCallback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println("cannot go to server");
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            response.close();
        }
    };
    public final Callback removeRoleCallback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println("cannot go to server");
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            response.close();
        }
    };
}