package com.perforce.sa;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;

public class AnalysisBuilderConfig extends AbstractDescribableImpl<AnalysisBuilderConfig> {

    private final String validateProjectURL;
    private final String validateApiUser;
    private final Secret validateApiToken;
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
    public AnalysisBuilderConfig(
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
            Secret validateApiToken,
            boolean enableQualityGate,
            String restrictionFileList) {

        this.validateProjectURL = validateProjectURL;
        this.validateApiUser = validateApiUser;
        this.validateApiToken = validateApiToken;
        this.engine = engine;
        this.analysisType = analysisType;
        this.scanBuildName = scanBuildName;
        this.searchQuery = searchQuery;
        this.jobResult = jobResult;
        this.enableQualityGate = enableQualityGate;
        this.usingBuildCmd = usingBuildCmd;
        this.buildCmd = buildCmd;
        this.usingBuildCaptureFile = usingBuildCaptureFile;
        this.buildCaptureFile = buildCaptureFile;
        this.restrictionFileList = restrictionFileList;
    }

    public AnalysisBuilderConfig(AnalysisBuilderPipelineConfig config) {
        this.validateProjectURL = config.getValidateProjectURL();
        this.validateApiUser = config.getValidateApiUser();
        this.validateApiToken = config.getValidateApiTokenSecret();
        this.engine = config.getEngine();
        this.analysisType = config.getAnalysisType();
        this.scanBuildName = config.getScanBuildName();
        this.searchQuery = config.getSearchQuery();
        this.jobResult = config.getJobResult();
        this.enableQualityGate = config.isEnableQualityGate();
        this.usingBuildCmd = config.isUsingBuildCmd();
        this.buildCmd = config.getBuildCmd();
        this.usingBuildCaptureFile = config.isUsingBuildCaptureFile();
        this.buildCaptureFile = config.getBuildCaptureFile();
        this.restrictionFileList = config.getRestrictionFileList();
    }

    public String getValidateProjectURL() {
        return validateProjectURL;
    }

    public Secret getValidateApiToken() {
        return validateApiToken;
    }

    public String getValidateApiTokenPlain() {
        return validateApiToken.getPlainText();
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

    public String getRestrictionFileList() {
        return restrictionFileList;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AnalysisBuilderConfig> {
        public String getDisplayName() {
            return "";
        }
    }
}
