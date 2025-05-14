package com.perforce.sa;

import hudson.model.Action;

public class AnalysisProjectDashboard implements Action {

    private final AnalysisBuilderConfig analysisConfig;

    public AnalysisProjectDashboard(AnalysisBuilderConfig analysisConfig) {
        this.analysisConfig = analysisConfig;
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
        return getValidateProjectUrlLink();
    }

    @Override
    public String getDisplayName() {
        return "Perforce Validate";
    }

    @Override
    public String getIconFileName() {
        return "symbol-logo-perforce-icon-reg plugin-p4sa";
    }
}
