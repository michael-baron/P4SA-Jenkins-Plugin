package com.perforce.sa;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilityFunctions {

    public static int executeCommand(
            Launcher launcher,
            TaskListener listener,
            FilePath buildDir,
            EnvVars envVars,
            ArgumentListBuilder cmds,
            boolean ignoreReturnCode)
            throws AbortException {
        if (launcher.isUnix()) {
            cmds = new ArgumentListBuilder("/bin/sh", "-c", cmds.toString());
        } else {
            cmds.add("&&", "exit", "%%ERRORLEVEL%%");
            cmds = new ArgumentListBuilder("cmd.exe", "/C", cmds.toString());
        }
        try {
            int returnCode = launcher.launch()
                    .stdout(listener)
                    .stderr(listener.getLogger())
                    .pwd(buildDir)
                    .envs(envVars)
                    .cmds(cmds)
                    .join();
            listener.getLogger().println("Return code: " + Integer.toString(returnCode));
            if (!ignoreReturnCode && returnCode != 0) {
                throw new AbortException("Non-zero Return Code. Aborting.");
            } else {
                return returnCode;
            }
        } catch (IOException | InterruptedException ex) {
            throw new AbortException(ex.getMessage());
        }
    }

    public static BufferedReader executeCommandParseOutput(
            Launcher launcher, FilePath buildDir, EnvVars envVars, ArgumentListBuilder cmds) throws AbortException {
        if (launcher.isUnix()) {
            cmds = new ArgumentListBuilder("/bin/sh", "-c", cmds.toString());
        } else {
            cmds.add("&&", "exit", "%%ERRORLEVEL%%");
            cmds = new ArgumentListBuilder("cmd.exe", "/C", cmds.toString());
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            launcher.launch()
                    .stdout(outputStream)
                    .stderr(outputStream)
                    .pwd(buildDir)
                    .envs(envVars)
                    .cmds(cmds)
                    .join();
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            if (inputStream != null) {
                inputStream.close();
            }
            return bufferedReader;
        } catch (IOException | InterruptedException ex) {
            throw new AbortException(ex.getMessage());
        }
    }

    public static String getValidateProjectName(String validateProjectURL) {
        Pattern pattern = Pattern.compile("https?://[\\w.-]+:\\d*((?:/[\\w.-]+)+)");
        Matcher matcher = pattern.matcher(validateProjectURL);
        String projectName = "";
        if (matcher.find() && matcher.groupCount() > 0) {
            projectName = matcher.group(1);
        }
        if (!projectName.isEmpty()) {
            projectName = projectName.substring(1);
        }
        return projectName;
    }

    public static String getValidateServerURL(String validateProjectURL) {
        Pattern pattern = Pattern.compile("(https?://[\\w.-]+:\\d*)(?:/[\\w.-]+)+");
        Matcher matcher = pattern.matcher(validateProjectURL);
        String serverURL = "";
        if (matcher.find() && matcher.groupCount() > 0) {
            serverURL = matcher.group(1);
        }
        return serverURL;
    }

    public static String formatAPIRequest(String action, HashMap<String, String> args) throws AbortException {
        String request = "&action=" + action;
        if (!args.isEmpty()) {
            try {
                for (HashMap.Entry<String, String> entry : args.entrySet()) {
                    request += "&" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");
                }
            } catch (UnsupportedEncodingException ex) {
                throw new AbortException(ex.getMessage());
            }
        }
        return request;
    }

    public static String resolveEnvVarsInConfig(EnvVars env, String input) {
        String output = input;
        for (HashMap.Entry<String, String> entry : env.entrySet()) {
            if (output.contains("$" + entry.getKey())) {
                output = output.replace("$" + entry.getKey(), entry.getValue());
            }
        }
        return output;
    }

    public static String[] splitOnSpaceQuotes(String input) {
        ArrayList<String> argsList = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input);
        while (m.find()) argsList.add(m.group(1).replace("\"", ""));
        return Arrays.copyOf(argsList.toArray(), argsList.size(), String[].class);
    }

    public static String getValidateProjectId(
            AnalysisBuilderConfig analysisConfig, Launcher launcher, FilePath buildDir, EnvVars envVars)
            throws AbortException {
        if (analysisConfig.getAnalysisType().equals("Delta")) {
            if (analysisConfig.getEngine().equals("Klocwork")) {
                ArgumentListBuilder cmd = new ArgumentListBuilder("kwciagent", "info");
                BufferedReader response = UtilityFunctions.executeCommandParseOutput(launcher, buildDir, envVars, cmd);
                if (response != null) {
                    String line = null;
                    try {
                        while ((line = response.readLine()) != null) {
                            if (!line.trim().equals("") && line.trim().startsWith("review.path=")) {
                                Pattern pattern = Pattern.compile("project\\=(.*),");
                                Matcher matcher = pattern.matcher(line.trim());
                                if (matcher.find()) {
                                    return matcher.group(0);
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new AbortException(e.getMessage());
                    }
                }
            }
        } else {
            if (analysisConfig.getEngine().equals("Klocwork")) {
                ArgumentListBuilder cmd = new ArgumentListBuilder("kwadmin", "list-projects", "-s", "-f");
                BufferedReader response = UtilityFunctions.executeCommandParseOutput(launcher, buildDir, envVars, cmd);
                if (response != null) {
                    String line = null;
                    try {
                        String id = "";
                        while ((line = response.readLine()) != null) {
                            if (!line.trim().equals("")) {
                                if (line.trim().contains("\"id\":")) {
                                    Pattern pattern = Pattern.compile("\"id\": \"(.*)\"");
                                    Matcher matcher = pattern.matcher(line.trim());
                                    if (matcher.find() && matcher.group(0) != null) {
                                        id = matcher.group(0);
                                    }
                                } else if (line.trim().contains("\"name\":")) {
                                    Pattern pattern = Pattern.compile("\"name\": \"(.*)\"");
                                    Matcher matcher = pattern.matcher(line.trim());
                                    if (matcher.find() && matcher.group(0) != null) {
                                        String name = matcher.group(0);
                                        if (name.equals(UtilityFunctions.getValidateProjectName(
                                                analysisConfig.getValidateProjectURL()))) {
                                            if (!id.equals("")) {
                                                return id;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new AbortException(e.getMessage());
                    }
                }
            }
        }
        return UtilityFunctions.getValidateProjectName(analysisConfig.getValidateProjectURL());
    }
}
