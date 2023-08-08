package Requester.flow.flowNames;

import Utils.Constants;
import okhttp3.Request;

public class FlowsNamesRequestImpl implements FlowsNamesRequest {
    private final String FLOW_URL = Constants.BASE_URL + Constants.FLOWS_NAMES;

    @Override
    public Request getAllFlowNamesRequest() {
        return new Request.Builder()
                .url(FLOW_URL)
                .get()
                .build();
    }
}
