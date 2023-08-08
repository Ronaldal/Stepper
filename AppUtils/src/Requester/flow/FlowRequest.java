package Requester.flow;

import okhttp3.Request;

public interface FlowRequest {
    Request getAllFlowRequest();
    Request getFlowRequest(String flowName);
}
