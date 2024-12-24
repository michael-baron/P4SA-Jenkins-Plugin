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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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

    public static ByteArrayOutputStream executeCommandParseOutput(
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
            return outputStream;
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

    public static JSONArray getJSONRespose(String request, String ltoken, String user, String url)
            throws AbortException {
        JSONArray response;
        try {
            ValidateAPIConnector apiConnection = new ValidateAPIConnector(url, user, ltoken);
            response = apiConnection.sendRequest(request);
        } catch (IOException ex) {
            throw new AbortException("Error: failed to connect to the Validate API.\nCause: " + ex.getMessage());
        }
        return response;
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

    public static String getValidateProjectId(AnalysisBuilderConfig analysisConfig) throws AbortException {
        HashMap<String, String> args = new HashMap<>();
        args.put("include_streams", "true");
        String request = UtilityFunctions.formatAPIRequest("projects", args);
        JSONArray response = UtilityFunctions.getJSONRespose(
                request,
                analysisConfig.getValidateApiTokenPlain(),
                analysisConfig.getValidateApiUser(),
                UtilityFunctions.getValidateServerURL(analysisConfig.getValidateProjectURL()));
        for (int i = 0; i < response.size(); i++) {
            JSONObject jObj = response.getJSONObject(i);
            if (jObj.getString("name")
                    .equals(UtilityFunctions.getValidateProjectName(analysisConfig.getValidateProjectURL()))) {
                return jObj.getString("id");
            }
        }
        return "";
    }
}
