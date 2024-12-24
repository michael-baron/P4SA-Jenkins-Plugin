package com.perforce.sa;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;

public class AnalysisBuilderPipelineConfig extends AbstractDescribableImpl<AnalysisBuilderPipelineConfig> {

    private final String validateProjectURL;
    private final String validateApiUser;
    private final String validateApiToken;
    private final String engine;
    private final String analysisType;
    private final boolean usingBuildCmd;
    private final String buildCmd;
    private boolean usingBuildCaptureFile;
    private final String buildCaptureFile;
    private final String scanBuildName;
    private final String searchQuery;
    private final boolean enableQualityGate;
    private final String jobResult;
    private final String restrictionFileList;

    @DataBoundConstructor
    public AnalysisBuilderPipelineConfig(
            String engine,
            String analysisType,
            boolean usingBuildCmd,
            String buildCmd,
            boolean usingBuildCaptureFile,
            String buildCaptureFile,
            String scanBuildName,
            String searchQuery,
            String jobResult,
            String validateProjectURL,
            String validateApiUser,
            String validateApiToken,
            boolean enableQualityGate,
            String restrictionFileList) {
        this.validateProjectURL = validateProjectURL;
        this.validateApiUser = validateApiUser;
        this.validateApiToken = validateApiToken;
        this.engine = engine;
        this.analysisType = analysisType;
        this.scanBuildName = scanBuildName;
        this.searchQuery = searchQuery;
        if (!enableQualityGate && jobResult == null) {
            this.jobResult = "";
        } else {
            this.jobResult = jobResult;
        }
        this.enableQualityGate = enableQualityGate;
        this.usingBuildCmd = usingBuildCmd;
        this.buildCmd = buildCmd;
        this.usingBuildCaptureFile = usingBuildCaptureFile;
        this.buildCaptureFile = buildCaptureFile;
        this.restrictionFileList = restrictionFileList;
    }

    public String getRestrictionFileList() {
        return restrictionFileList;
    }

    public String getValidateProjectURL() {
        return validateProjectURL;
    }

    public Secret getValidateApiTokenSecret() {
        return Secret.fromString(validateApiToken);
    }

    public String getValidateApiToken() {
        return validateApiToken;
    }

    public String getValidateApiUser() {
        return validateApiUser;
    }

    public String getEngine() {
        return engine;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public String getScanBuildName() {
        return scanBuildName;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public boolean isEnableQualityGate() {
        return enableQualityGate;
    }

    public String getJobResult() {
        return jobResult;
    }

    public boolean isUsingBuildCaptureFile() {
        return usingBuildCaptureFile;
    }

    public void setUsingBuildCaptureFile(boolean value) {
        usingBuildCaptureFile = value;
    }

    public String getBuildCaptureFile() {
        return buildCaptureFile;
    }

    public boolean isUsingBuildCmd() {
        return usingBuildCmd;
    }

    public String getBuildCmd() {
        return buildCmd;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AnalysisBuilderPipelineConfig> {
        public String getDisplayName() {
            return "";
        }
    }
}
