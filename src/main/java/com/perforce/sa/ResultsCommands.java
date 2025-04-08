package com.perforce.sa;

import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;

public class ResultsCommands {

    public static ArgumentListBuilder getResultsCommand(
            TaskListener listener,
            String engine,
            String type,
            boolean usingBuildCmd,
            String buildCmd,
            boolean usingBuildCaptureFile,
            String buildCaptureFile,
            String validateProjectURL,
            String buildName,
            String restrictionList) {
        ArgumentListBuilder cmds = null;
        if (type.equals("Delta")) {
            if (engine.equals("QAC")) {
                cmds = createQACResultCMD(usingBuildCmd, buildCmd, buildCaptureFile, validateProjectURL, buildName);
            } else if (engine.equals("Klocwork")) {
                cmds = createKlocworkResultCMD();
            } else {
                listener.getLogger()
                        .println(
                                "ERROR: delta analysis selected, but engine selection does not equal QAC or Klocwork. engine="
                                        + engine);
            }
        } else {
            listener.getLogger().println("ERROR: unsupported analysis type selected, analysisType=" + type);
        }
        return cmds;
    }

    private static ArgumentListBuilder createQACResultCMD(
            boolean usingBuildCmd,
            String buildCmd,
            String buildCaptureFile,
            String validateProjectURL,
            String buildName) {
        ArgumentListBuilder cmds = new ArgumentListBuilder("qacli", "validate", "cibuild", "-P", ".", "-b", buildName);
        return cmds;
    }

    private static ArgumentListBuilder createKlocworkResultCMD() {
        ArgumentListBuilder cmds = new ArgumentListBuilder("kwciagent", "list", "-F", "scriptable");
        return cmds;
    }
}
