package Requester.Stats;

import okhttp3.Request;

public interface FlowStatsRequest {
    Request getAllFlowRequest();
    Request getFlowRequest(String flowName);
    Request getStatsByVersion(int version);
}
