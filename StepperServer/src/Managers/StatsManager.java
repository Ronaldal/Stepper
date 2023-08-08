package Managers;

import DTO.ExecutionsStatistics.FlowExecutionStats;

import java.util.ArrayList;
import java.util.List;

public class StatsManager {
    private List<FlowExecutionStats> flowExecutionStatsList=new ArrayList<>();
    private int version=0;

    public synchronized void setFlowExecutionStatsList(List<FlowExecutionStats> flowExecutionStatsList) {
        this.flowExecutionStatsList = flowExecutionStatsList;
    }

    public synchronized List<FlowExecutionStats> getStatsEntries(){
        return flowExecutionStatsList;
    }

    public int getVersion() {
        return version;
    }

    public synchronized void addVersion() {
        this.version++;
    }


}
