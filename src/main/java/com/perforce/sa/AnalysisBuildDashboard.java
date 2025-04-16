package com.perforce.sa;

import hudson.EnvVars;
import hudson.model.Action;

public class AnalysisBuildDashboard implements Action {

    public final String url;
    public final String text;
    public final String icon;
    private final EnvVars env;
    private final AnalysisBuilderConfig analysisConfig;

    public AnalysisBuildDashboard(EnvVars env, AnalysisBuilderConfig analysisConfig) {
        this.text = analysisConfig.getEngine() + " Scan Results";
        this.icon = "/plugin/p4sa/icon/logo-perforce-icon-reg.svg";
        this.env = env;
        this.analysisConfig = analysisConfig;
        this.url = getValidateBuildUrlLink();
    }

    public String getValidateBuildUrlLink() {
        String validatePortalURL = "";
        String projectUrlName = "";
        if (getAnalysisConfig().getValidateProjectId() == null) {
            projectUrlName = getAnalysisConfig().getValidateProjectName();
        } else {
            projectUrlName = getAnalysisConfig().getValidateProjectId();
        }
        if (getAnalysisConfig().getAnalysisType().equals("Baseline")) {
            validatePortalURL =
                    UtilityFunctions.getValidateServerURL(getAnalysisConfig().getValidateProjectURL())
                            + "/review/insight-review.html#issuelist_goto:project="
                            + projectUrlName
                            + ",searchquery=build%253A'"
                            + UtilityFunctions.resolveEnvVarsInConfig(
                                    getEnv(), getAnalysisConfig().getScanBuildName())
                            + "'";
        } else {
            validatePortalURL =
                    UtilityFunctions.getValidateServerURL(getAnalysisConfig().getValidateProjectURL())
                            + "/review/insight-review.html#issuelist_goto:project="
                            + projectUrlName
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
