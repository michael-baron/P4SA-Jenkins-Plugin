package com.perforce.sa;

import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class AnalysisBuilderConfig extends AbstractDescribableImpl<AnalysisBuilderConfig> {

    private final String validateProjectURL;
    private final String engine;
    private final String analysisType;
    private final boolean usingBuildCmd;
    private final String buildCmd;
    private boolean usingBuildCaptureFile;
    private final String buildCaptureFile;
    private final String scanBuildName;
    private final boolean enableQualityGate;
    private String jobResult;
    private final String restrictionFileList;
    private String validateProjectId;
    private final String validateProjectName;

    @DataBoundConstructor
    public AnalysisBuilderConfig(
            String engine,
            String analysisType,
            boolean usingBuildCmd,
            String buildCmd,
            boolean usingBuildCaptureFile,
            String buildCaptureFile,
            String scanBuildName,
            String jobResult,
            String validateProjectURL,
            boolean enableQualityGate,
            String restrictionFileList,
            String validateProjectName) {

        this.validateProjectURL = validateProjectURL;
        this.engine = engine;
        this.analysisType = analysisType;
        this.scanBuildName = scanBuildName;
        this.jobResult = jobResult;
        this.enableQualityGate = enableQualityGate;
        this.usingBuildCmd = usingBuildCmd;
        this.buildCmd = buildCmd;
        this.usingBuildCaptureFile = usingBuildCaptureFile;
        this.buildCaptureFile = buildCaptureFile;
        this.restrictionFileList = restrictionFileList;
        this.validateProjectName = UtilityFunctions.getValidateProjectName(validateProjectURL);
    }

    public String getValidateProjectURL() {
        return validateProjectURL;
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

    public String getValidateProjectId() {
        return validateProjectId;
    }

    public void setValidateProjectId(String value) {
        validateProjectId = value;
    }

    public String getValidateProjectName() {
        return validateProjectName;
    }

    @DataBoundSetter
    public void setJobResult(String jobResult) {
        this.jobResult = Util.fixEmpty(jobResult);
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AnalysisBuilderConfig> {
        public String getDisplayName() {
            return "";
        }
    }
}
