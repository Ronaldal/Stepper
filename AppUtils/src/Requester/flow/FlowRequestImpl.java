package Requester.flow;

import Utils.Constants;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class FlowRequestImpl implements FlowRequest {

    private final String FLOW_URL = Constants.BASE_URL + Constants.FLOW_URL;
    @Override
    public Request getAllFlowRequest() {
        return new Request.Builder()
                .url(FLOW_URL)
                .get()
                .build();
    }

    @Override
    public Request getFlowRequest(String flowName) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(FLOW_URL).newBuilder();
        urlBuilder.addQueryParameter("flow_name", flowName);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();

    }
}
