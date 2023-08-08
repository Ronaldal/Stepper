package JavaFx;

import Utils.SimpleCookieManager;
import okhttp3.OkHttpClient;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientUtils {

    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/login/login.fxml";

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();

    public final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();
    public ClientUtils(){
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
    }
    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}
