package servlets.stats;

import DTO.ExecutionsStatistics.FlowExecutionStats;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import Managers.StatsManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@WebServlet(name = "getStatsWithVersionFromEngine", urlPatterns = "/stats_version")
public class StatsVersionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        StatsManager statsManager = ServletUtils.getStatsManager(getServletContext());
        //int chatVersion = ServletUtils.getIntParameter(req, "stats_version");
        List<FlowExecutionStats> entries;
        int version;
        synchronized (getServletContext()) {
            version = statsManager.getVersion();
            entries= statsManager.getStatsEntries();
        }
        StatsAndVersion statsAndVersion=new StatsAndVersion(entries,version);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(statsAndVersion);
        try (PrintWriter out = resp.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    private static class StatsAndVersion {

        final private List<FlowExecutionStats> entries;
        final private int version;

        public StatsAndVersion(List<FlowExecutionStats> entries, int version) {
            this.entries = entries;
            this.version = version;
        }
    }
}

