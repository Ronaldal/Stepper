package AdminUtils;

import Requester.Roles.RoleRequestImpl;
import Requester.Stats.FlowStatsRequestImp;
import Requester.Users.UsersRequesterImpl;
import Requester.execution.ExecutionRequestImpl;
import Requester.fileupload.FileUploadImpl;
import Requester.flow.flowNames.FlowsNamesRequestImpl;
import Utils.Constants;
import Utils.SimpleCookieManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import users.roles.RoleImpl;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminUtils {

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();

    public final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .cookieJar(simpleCookieManager)
            .followRedirects(false)
            .build();

    public final static FileUploadImpl FILE_UPLOAD=new FileUploadImpl();
    public final static FlowsNamesRequestImpl FLOWS_NAMES_REQUEST=new FlowsNamesRequestImpl();
    public final static FlowStatsRequestImp FLOW_STATS_REQUEST=new FlowStatsRequestImp();
    public final static ExecutionRequestImpl EXECUTION_REQUEST=new ExecutionRequestImpl();
    public final static RoleRequestImpl ROLE_REQUEST=new RoleRequestImpl();
    public final static UsersRequesterImpl USERS_REQUESTER=new UsersRequesterImpl();

    public AdminUtils(){
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
    }

    public static List<RoleImpl> getRoles(Request request, OkHttpClient httpClient){
        try {
            Response response = httpClient.newCall(request).execute();
            if(response.isSuccessful()){
                Gson gson= Constants.GSON_INSTANCE;
                Type listType = new TypeToken<List<RoleImpl>>() {}.getType();
                List<RoleImpl> roleList = gson.fromJson(response.body().string(), listType);
                return roleList;
            }else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
