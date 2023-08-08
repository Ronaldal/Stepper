package Requester.execution;

import Utils.Constants;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ExecutionRequestImpl implements ExecutionRequest {

    private static final String EXECUTION_URL = Constants.BASE_URL + Constants.EXECUTE_FLOW_URL;
    private static final String READY_URL = Constants.BASE_URL + Constants.READY_URL;
    private static final String STATUS_URL = Constants.BASE_URL + Constants.EXECUTION_STATUS_URL;
    private static final String RERUN_URL = Constants.BASE_URL + Constants.EXECUTION_RERUN_URL;
    private static final String DATA_URL = Constants.BASE_URL + Constants.EXECUTION_DATA_URL;
    private static final String FREE_INPUTS_URL = Constants.BASE_URL + Constants.FREE_INPUTS;
    private static final String CONTINUATION_URL = Constants.BASE_URL + Constants.CONTINUATION_URL;
    @Override
    public Request isExecutionReadyRequest(String uuid) {
        return getStatusRequest(READY_URL, uuid);
    }

    @Override
    public Request executionStatusRequest(String uuid) {
        return getStatusRequest(STATUS_URL, uuid);
    }

    private static Request getStatusRequest(String statusUrl, String uuid) {
        return getRequest(statusUrl, "uuid", uuid);
    }

    @Override
    public Request executionsHistoryRequest() {
        return new Request.Builder()
                .url(DATA_URL)
                .get()
                .build();
    }

    @Override
    public Request executeRequest(String uuid) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(EXECUTION_URL).newBuilder();
        urlBuilder.addQueryParameter("uuid", uuid);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .post(RequestBody.create(null, new byte[0]))
                .build();
    }

    @Override
    public Request createExecuteRequest(String flowName) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(EXECUTION_URL).newBuilder();
        urlBuilder.addQueryParameter("flow_name", flowName);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }

    @Override
    public Request addFreeInputRequest(String uuid, String dataName, String data) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(EXECUTION_URL).newBuilder();
        urlBuilder.addQueryParameter("uuid", uuid)
                .addQueryParameter("free-input", dataName)
                .addQueryParameter("data", data);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .put(RequestBody.create(null, new byte[0]))
                .build();
    }

    @Override
    public Request flowExecutionsRequest(String flowName) {
        return getRequest(DATA_URL, "flow_name", flowName);
    }

    @Override
    public Request executionRequest(String uuid) {
        return getRequest(DATA_URL, "uuid", uuid);
    }

    @Override
    public Request rerunRequest(String uuid) {
        return getRequest(RERUN_URL, "uuid", uuid);
    }

    private static Request getRequest(String executionUrl, String paramName, String paramValue) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(executionUrl).newBuilder();
        urlBuilder.addQueryParameter(paramName, paramValue);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }

    @Override
    public Request continuationRequest(String uuid, String flowToContinue) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(CONTINUATION_URL).newBuilder();
        urlBuilder.addQueryParameter("uuid", uuid)
                .addQueryParameter("flow_name", flowToContinue);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }

    @Override
    public Request executionDataList() {
        return new Request.Builder()
                .url(DATA_URL)
                .get()
                .build();
    }

    @Override
    public Request getFreeInputs(String uuid) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(FREE_INPUTS_URL).newBuilder();
        urlBuilder.addQueryParameter("uuid", uuid);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }
}
