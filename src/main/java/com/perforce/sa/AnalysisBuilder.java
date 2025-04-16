package com.perforce.sa;

import hudson.*;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class AnalysisBuilder extends Builder implements SimpleBuildStep {

    private final AnalysisBuilderConfig analysisConfig;

    @DataBoundConstructor
    public AnalysisBuilder(AnalysisBuilderConfig analysisConfig) {
        this.analysisConfig = analysisConfig;
    }

    public AnalysisBuilderConfig getAnalysisConfig() {
        return analysisConfig;
    }

    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
        List<Action> actions = new ArrayList<>();
        actions.add(new AnalysisProjectDashboard(getAnalysisConfig()));
        return actions;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {
        if (getAnalysisConfig().isUsingBuildCmd() && getAnalysisConfig().isUsingBuildCaptureFile()) {
            listener.getLogger()
                    .println("Can not use both a build command and build capture file, only using build command");
            getAnalysisConfig().setUsingBuildCaptureFile(false);
        }
        listener.getLogger().println("Starting Perforce Static Analysis Scan");
        for (ArgumentListBuilder cmd : AnalysisCommands.getAnalysisCommand(
                listener,
                getAnalysisConfig().getEngine(),
                getAnalysisConfig().getAnalysisType(),
                getAnalysisConfig().isUsingBuildCmd(),
                getAnalysisConfig().getBuildCmd(),
                getAnalysisConfig().isUsingBuildCaptureFile(),
                getAnalysisConfig().getBuildCaptureFile(),
                getAnalysisConfig().getValidateProjectURL(),
                getAnalysisConfig().getScanBuildName(),
                getAnalysisConfig().getRestrictionFileList())) {
            if (getAnalysisConfig().getEngine().equalsIgnoreCase("qac")
                    && getAnalysisConfig().isEnableQualityGate()
                    && getAnalysisConfig().getAnalysisType().equalsIgnoreCase("delta")) {
                BufferedReader response = UtilityFunctions.executeCommandParseOutput(launcher, workspace, env, cmd);
                if (response != null) {
                    String line = null;
                    while ((line = response.readLine()) != null) {
                        listener.getLogger().println(line);
                        if (line.trim().toLowerCase().contains("build status: unstable")) {
                            listener.getLogger().println("issues found for quality gate");
                            if (getAnalysisConfig().getJobResult().equals("Failure")) {
                                run.setResult(Result.FAILURE);
                            } else if (getAnalysisConfig().getJobResult().equals("Unstable")) {
                                run.setResult(Result.UNSTABLE);
                            }
                        }
                    }
                }
            } else {
                UtilityFunctions.executeCommand(launcher, listener, workspace, env, cmd, false);
            }
        }
        if (getAnalysisConfig().isEnableQualityGate()
                && getAnalysisConfig().getAnalysisType().equalsIgnoreCase("delta")
                && getAnalysisConfig().getEngine().equalsIgnoreCase("klocwork")) {
            listener.getLogger().println("Quality gate is enabled and getting results from the delta scan");
            ArgumentListBuilder cmd = ResultsCommands.getResultsCommand(
                    listener,
                    getAnalysisConfig().getEngine(),
                    getAnalysisConfig().getAnalysisType(),
                    getAnalysisConfig().isUsingBuildCmd(),
                    getAnalysisConfig().getBuildCmd(),
                    getAnalysisConfig().isUsingBuildCaptureFile(),
                    getAnalysisConfig().getBuildCaptureFile(),
                    getAnalysisConfig().getValidateProjectURL(),
                    getAnalysisConfig().getScanBuildName(),
                    getAnalysisConfig().getRestrictionFileList());
            BufferedReader response = UtilityFunctions.executeCommandParseOutput(launcher, workspace, env, cmd);
            if (response != null) {
                String line = null;
                int localIssues = 0;
                while ((line = response.readLine()) != null) {
                    if (!line.trim().equals("")) {
                        localIssues++;
                    }
                }
                if (localIssues == 0) {
                    listener.getLogger().println("No issues found for quality gate");
                } else {
                    listener.getLogger().println(localIssues + " issues found for quality gate");
                    if (getAnalysisConfig().getJobResult().equals("Failure")) {
                        run.setResult(Result.FAILURE);
                    } else if (getAnalysisConfig().getJobResult().equals("Unstable")) {
                        run.setResult(Result.UNSTABLE);
                    }
                }
            }
        }
        listener.getLogger().println("Adding the build dashboard");
        getAnalysisConfig()
                .setValidateProjectId(
                        UtilityFunctions.getValidateProjectId(getAnalysisConfig(), launcher, workspace, env));
        run.addAction(new AnalysisBuildDashboard(env, getAnalysisConfig()));
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public String getDisplayName() {
            return "Run Perforce Static Analysis";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
