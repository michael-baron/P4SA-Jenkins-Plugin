package com.perforce.sa;

// import edu.hm.hafner.echarts.*;
import hudson.AbortException;
import hudson.model.Action;
import java.util.ArrayList;
import java.util.HashMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AnalysisProjectDashboard implements Action {

    public final String url;
    public final String text;
    public final String icon;
    private final AnalysisBuilderConfig analysisConfig;

    public AnalysisProjectDashboard(AnalysisBuilderConfig analysisConfig) {
        this.url = "P4SADashboard";
        this.text = "P4 SA Dashboard";
        this.icon = "/plugin/p4sa-plugin/icon/p4.png";
        this.analysisConfig = analysisConfig;
    }

    public String getChartData() throws AbortException {
        JSONObject chartData = new JSONObject();
        ArrayList<String> labels = new ArrayList<>();
        JSONArray dataset = new JSONArray();
        JSONObject openIssuesData = new JSONObject();
        openIssuesData.put("label", "Open Issues");
        JSONArray openIssuesRows = new JSONArray();
        HashMap<String, String> buildsArgs = new HashMap<String, String>();
        buildsArgs.put(
                "project",
                UtilityFunctions.getValidateProjectName(getAnalysisConfig().getValidateProjectURL()));
        String buildsRequest = UtilityFunctions.formatAPIRequest("builds", buildsArgs);
        JSONArray buildsResponse = UtilityFunctions.getJSONRespose(
                buildsRequest,
                getAnalysisConfig().getValidateApiTokenPlain(),
                getAnalysisConfig().getValidateApiUser(),
                UtilityFunctions.getValidateServerURL(getAnalysisConfig().getValidateProjectURL()));
        for (int b = buildsResponse.size() - 1; b >= 0; b--) {
            JSONObject bJObj = buildsResponse.getJSONObject(b);
            HashMap<String, String> args = new HashMap<String, String>();
            args.put(
                    "query",
                    "build:'" + bJObj.getString("name") + "' "
                            + getAnalysisConfig().getSearchQuery());
            args.put(
                    "project",
                    UtilityFunctions.getValidateProjectName(getAnalysisConfig().getValidateProjectURL()));
            String request = UtilityFunctions.formatAPIRequest("search", args);
            JSONArray response = UtilityFunctions.getJSONRespose(
                    request,
                    getAnalysisConfig().getValidateApiTokenPlain(),
                    getAnalysisConfig().getValidateApiUser(),
                    UtilityFunctions.getValidateServerURL(getAnalysisConfig().getValidateProjectURL()));
            int issuesNum = 0;
            labels.add(bJObj.getString("name"));
            for (int i = 0; i < response.size(); i++) {
                JSONObject jObj = response.getJSONObject(i);
                switch (jObj.getString("status")) {
                    case "Analyze":
                        issuesNum++;
                        break;
                    case "Fix":
                        issuesNum++;
                        break;
                    default:
                        break;
                }
            }
            openIssuesRows.add(issuesNum);
        }
        chartData.put("labels", labels);
        openIssuesData.put("data", openIssuesRows);
        openIssuesData.put("borderColor", "#dc0d0e");
        openIssuesData.put("backgroundColor", "rgba(0,0,0,0)");
        dataset.add(openIssuesData);
        chartData.put("datasets", dataset);
        String returnString = chartData.toString();
        return returnString;
    }

    public String getValidateProjectUrlLink() throws AbortException {
        String validatePortalURL =
                UtilityFunctions.getValidateServerURL(getAnalysisConfig().getValidateProjectURL())
                        + "/review/insight-review.html#reportviewer_goto:project="
                        + UtilityFunctions.getValidateProjectId(getAnalysisConfig());
        return validatePortalURL;
    }

    public AnalysisBuilderConfig getAnalysisConfig() {
        return analysisConfig;
    }

    public String getHeaderTitle() {
        String engine = "";
        if (getAnalysisConfig() != null && getAnalysisConfig().getEngine() != null) {
            engine = getAnalysisConfig().getEngine();
        }
        return "P4 SA (" + engine + ") Results";
    }

    @Override
    public String getUrlName() {
        return url;
    }

    @Override
    public String getDisplayName() {
        return text;
    }

    @Override
    public String getIconFileName() {
        return icon;
    }
}
