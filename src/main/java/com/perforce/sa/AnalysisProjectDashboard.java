package com.perforce.sa;

import hudson.model.Action;

public class AnalysisProjectDashboard implements Action {

    public final String url;
    public final String text;
    public final String icon;
    private final AnalysisBuilderConfig analysisConfig;

    public AnalysisProjectDashboard(AnalysisBuilderConfig analysisConfig) {
        this.analysisConfig = analysisConfig;
        this.url = getValidateProjectUrlLink();
        this.text = "P4 SA Dashboard";
        this.icon = "/plugin/p4sa/icon/logo-perforce-icon-reg.svg";
    }

    public String getValidateProjectUrlLink() {
        String projectURLName = "";
        if (getAnalysisConfig().getValidateProjectId() == null) {
            projectURLName = getAnalysisConfig().getValidateProjectName();
        } else {
            projectURLName = getAnalysisConfig().getValidateProjectId();
        }
        String validatePortalURL =
                UtilityFunctions.getValidateServerURL(getAnalysisConfig().getValidateProjectURL())
                        + "/review/insight-review.html#reportviewer_goto:project="
                        + projectURLName;
        return validatePortalURL;
    }

    public AnalysisBuilderConfig getAnalysisConfig() {
        return analysisConfig;
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
