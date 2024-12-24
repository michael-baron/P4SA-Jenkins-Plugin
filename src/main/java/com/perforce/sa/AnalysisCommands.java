package com.perforce.sa;

import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;
import java.util.ArrayList;

public class AnalysisCommands {

    public static ArrayList<ArgumentListBuilder> getAnalysisCommand(
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
        ArrayList<ArgumentListBuilder> cmds = new ArrayList<>();
        if (type.equals("Baseline")) {
            if (engine.equals("QAC")) {
                cmds = createQACBaselineCMD(usingBuildCmd, buildCmd, buildCaptureFile, validateProjectURL, buildName);
            } else if (engine.equals("Klocwork")) {
                cmds = createKlocworkBaselineCMD(
                        usingBuildCmd, buildCmd, buildCaptureFile, validateProjectURL, buildName);
            } else {
                listener.getLogger()
                        .println(
                                "ERROR: baseline analysis selected, but engine selection does not equal QAC or Klocwork. engine="
                                        + engine);
            }
        } else if (type.equals("Delta")) {
            if (engine.equals("QAC")) {
                cmds = createQACDeltaCMD(usingBuildCmd, buildCmd, buildCaptureFile, validateProjectURL, buildName);
            } else if (engine.equals("Klocwork")) {
                cmds = createKlocworkDeltaCMD(
                        usingBuildCmd, buildCmd, buildCaptureFile, validateProjectURL, buildName, restrictionList);
            } else {
                listener.getLogger()
                        .println(
                                "ERROR: delta analysis selected, but engine selection does not equal QAC or Klocwork. engine="
                                        + engine);
            }
        } else {
            listener.getLogger().println("ERROR: unknown analysis type selected, analysisType=" + type);
        }
        return cmds;
    }

    private static ArrayList<ArgumentListBuilder> createQACBaselineCMD(
            boolean usingBuildCmd,
            String buildCmd,
            String buildCaptureFile,
            String validateProjectURL,
            String buildName) {
        ArrayList<ArgumentListBuilder> cmds = new ArrayList<>();
        cmds.add(new ArgumentListBuilder(
                "qacli",
                "validate",
                "config",
                "-c",
                "-P",
                ".",
                "-U",
                UtilityFunctions.getValidateServerURL(validateProjectURL),
                "-b",
                UtilityFunctions.getValidateProjectName(validateProjectURL)));
        if (usingBuildCmd) {
            String[] buildCmdSplit = buildCmd.split("\n");
            for (int i = 0; i < buildCmdSplit.length; i++) {
                if (i != buildCmdSplit.length - 1) {
                    cmds.add(new ArgumentListBuilder(UtilityFunctions.splitOnSpaceQuotes(buildCmdSplit[i])));
                } else {
                    cmds.add(new ArgumentListBuilder(
                            "qacli", "sync", "-P", ".", "-t", "INJECT", "-g", "\"" + buildCmdSplit[i] + "\""));
                }
            }
            cmds.add(new ArgumentListBuilder("qacli", "validate", "build", "-P", "."));
        } else {
            cmds.add(new ArgumentListBuilder("qacli", "validate", "build", "-P", "."));
        }
        return cmds;
    }

    private static ArrayList<ArgumentListBuilder> createQACDeltaCMD(
            boolean usingBuildCmd,
            String buildCmd,
            String buildCaptureFile,
            String validateProjectURL,
            String buildName) {
        ArrayList<ArgumentListBuilder> cmds = new ArrayList<>();
        cmds.add(new ArgumentListBuilder(
                "qacli",
                "validate",
                "config",
                "-c",
                "-P",
                ".",
                "-U",
                UtilityFunctions.getValidateServerURL(validateProjectURL),
                "-b",
                UtilityFunctions.getValidateProjectName(validateProjectURL)));
        if (usingBuildCmd) {
            String[] buildCmdSplit = buildCmd.split("\n");
            for (int i = 0; i < buildCmdSplit.length; i++) {
                if (i != buildCmdSplit.length - 1) {
                    cmds.add(new ArgumentListBuilder(UtilityFunctions.splitOnSpaceQuotes(buildCmdSplit[i])));
                } else {
                    cmds.add(new ArgumentListBuilder(
                            "qacli", "sync", "-P", ".", "-t", "INJECT", "-g", "\"" + buildCmdSplit[i] + "\""));
                }
            }
            cmds.add(new ArgumentListBuilder("qacli", "validate", "cibuild", "-P", ".", "-b", buildName));
        } else {
            cmds.add(new ArgumentListBuilder("qacli", "validate", "cibuild", "-P", ".", "-b", buildName));
        }
        return cmds;
    }

    private static ArrayList<ArgumentListBuilder> createKlocworkBaselineCMD(
            boolean usingBuildCmd,
            String buildCmd,
            String buildCaptureFile,
            String validateProjectURL,
            String buildName) {
        ArrayList<ArgumentListBuilder> cmds = new ArrayList<>();
        if (usingBuildCmd) {
            String[] buildCmdSplit = buildCmd.split("\n");
            for (int i = 0; i < buildCmdSplit.length; i++) {
                if (i != buildCmdSplit.length - 1) {
                    cmds.add(new ArgumentListBuilder(UtilityFunctions.splitOnSpaceQuotes(buildCmdSplit[i])));
                } else {
                    cmds.add(new ArgumentListBuilder(
                            UtilityFunctions.splitOnSpaceQuotes("kwinject " + buildCmdSplit[i])));
                }
            }
            cmds.add(new ArgumentListBuilder(
                    "kwbuildproject", "--url", validateProjectURL, "-o", "kwtables", "-I", "kwinject.out"));
        } else {
            cmds.add(new ArgumentListBuilder(
                    "kwbuildproject", "--url", validateProjectURL, "-o", "kwtables", "-I", buildCaptureFile));
        }
        cmds.add(new ArgumentListBuilder(
                "kwadmin",
                "--url",
                UtilityFunctions.getValidateServerURL(validateProjectURL),
                "load",
                UtilityFunctions.getValidateProjectName(validateProjectURL),
                "kwtables",
                "--name",
                buildName));
        return cmds;
    }

    private static ArrayList<ArgumentListBuilder> createKlocworkDeltaCMD(
            boolean usingBuildCmd,
            String buildCmd,
            String buildCaptureFile,
            String validateProjectURL,
            String buildName,
            String restrictionList) {
        ArrayList<ArgumentListBuilder> cmds = new ArrayList<>();
        if (usingBuildCmd) {
            String[] buildCmdSplit = buildCmd.strip().split("\n");
            for (int i = 0; i < buildCmdSplit.length; i++) {
                if (i != buildCmdSplit.length - 1) {
                    cmds.add(new ArgumentListBuilder(UtilityFunctions.splitOnSpaceQuotes(buildCmdSplit[i])));
                } else {
                    cmds.add(new ArgumentListBuilder(
                            UtilityFunctions.splitOnSpaceQuotes("kwinject " + buildCmdSplit[i])));
                }
            }
            cmds.add(new ArgumentListBuilder("kwciagent", "create", "--url", validateProjectURL, "-b", "kwinject.out"));
        } else {
            cmds.add(new ArgumentListBuilder(
                    "kwciagent", "create", "--url", validateProjectURL, "-b", buildCaptureFile));
        }
        if (restrictionList != null && !restrictionList.isEmpty()) {
            cmds.add(new ArgumentListBuilder("kwciagent", "run", "-c", buildName, "@" + restrictionList));
        } else {
            cmds.add(new ArgumentListBuilder("kwciagent", "run", "-c", buildName));
        }
        return cmds;
    }
}
