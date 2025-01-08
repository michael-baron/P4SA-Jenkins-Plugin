package com.perforce.sa;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.model.Action;

public class AnalysisBuildDashboard implements Action {

    public final String url;
    public final String text;
    public final String icon;
    private final EnvVars env;
    private final AnalysisBuilderConfig analysisConfig;

    public AnalysisBuildDashboard(EnvVars env, AnalysisBuilderConfig analysisConfig) {
        this.url = "P4SADashboard";
        this.text = "P4 SA Dashboard";
        this.icon = "/plugin/p4sa-plugin/icon/p4.png";
        this.env = env;
        this.analysisConfig = analysisConfig;
    }

    public String getValidateBuildUrlLink() throws AbortException {
        String validatePortalURL = "";
        if (getAnalysisConfig().getAnalysisType().equals("Baseline")) {
            validatePortalURL =
                    UtilityFunctions.getValidateServerURL(getAnalysisConfig().getValidateProjectURL())
                            + "/review/insight-review.html#issuelist_goto:project="
                            + UtilityFunctions.getValidateProjectId(getAnalysisConfig())
                            + ",searchquery=build%253A'"
                            + UtilityFunctions.resolveEnvVarsInConfig(
                                    getEnv(), getAnalysisConfig().getScanBuildName())
                            + "'";
        } else {
            validatePortalURL =
                    UtilityFunctions.getValidateServerURL(getAnalysisConfig().getValidateProjectURL())
                            + "/review/insight-review.html#issuelist_goto:project="
                            + UtilityFunctions.getValidateProjectId(getAnalysisConfig())
                            + ",searchquery=ci%253A'"
                            + UtilityFunctions.resolveEnvVarsInConfig(
                                    getEnv(), getAnalysisConfig().getScanBuildName())
                            + "'";
        }
        return validatePortalURL;
    }

    public AnalysisBuilderConfig getAnalysisConfig() {
        return analysisConfig;
    }

    public EnvVars getEnv() {
        return env;
    }

    public String getHeaderTitle() {
        return "P4 SA (" + getAnalysisConfig().getEngine() + ") Results";
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
