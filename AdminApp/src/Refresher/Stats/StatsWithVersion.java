package Refresher.Stats;

import DTO.ExecutionsStatistics.FlowExecutionStats;

import java.util.List;

public class StatsWithVersion {
    private List<FlowExecutionStats> entries;
    private int version;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<FlowExecutionStats> getEntries() {
        return entries;
    }

    public void setEntries(List<FlowExecutionStats> entries) {
        this.entries = entries;
    }

}
