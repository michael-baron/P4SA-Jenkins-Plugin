package com.perforce.sa;

import hudson.EnvVars;
import hudson.model.Action;

public class AnalysisBuildDashboard implements Action {

    private final EnvVars env;
    private final AnalysisBuilderConfig analysisConfig;

    public AnalysisBuildDashboard(EnvVars env, AnalysisBuilderConfig analysisConfig) {
        this.env = env;
        this.analysisConfig = analysisConfig;
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

    @Override
    public String getUrlName() {
        return getValidateBuildUrlLink();
    }

    @Override
    public String getDisplayName() {
        return analysisConfig.getEngine() + " Scan Results";
    }

    @Override
    public String getIconFileName() {
        return "symbol-logo-perforce-icon-reg plugin-p4sa";
    }
}
