package com.perforce.sa;

import com.google.inject.Inject;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import javax.annotation.Nonnull;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;

public class AnalysisPipelineBuilder extends AbstractStepImpl {

    private final AnalysisBuilderPipelineConfig analysisConfig;

    @DataBoundConstructor
    public AnalysisPipelineBuilder(AnalysisBuilderPipelineConfig analysisConfig) {
        this.analysisConfig = analysisConfig;
    }

    public AnalysisBuilderPipelineConfig getAnalysisConfig() {
        return analysisConfig;
    }

    private static class AnalysisPipelineBuilderExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

        private static final long serialVersionUID = 1L;

        @Inject
        private transient AnalysisPipelineBuilder step;

        @StepContextParameter
        @SuppressWarnings("unused")
        private transient Run run;

        @StepContextParameter
        @SuppressWarnings("unused")
        private transient FilePath workspace;

        @StepContextParameter
        @SuppressWarnings("unused")
        private transient Launcher launcher;

        @StepContextParameter
        @SuppressWarnings("unused")
        private transient TaskListener listener;

        @StepContextParameter
        private transient EnvVars env;

        @Override
        protected Void run() throws Exception {
            AnalysisBuilder builder = new AnalysisBuilder(new AnalysisBuilderConfig(step.getAnalysisConfig()));
            builder.perform(run, workspace, env, launcher, listener);
            return null;
        }
    }

    @Extension(optional = true)
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {
        public DescriptorImpl() {
            super(AnalysisPipelineBuilderExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "P4_StaticAnalysis";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Run Perforce Static Analysis";
        }
    }
}
