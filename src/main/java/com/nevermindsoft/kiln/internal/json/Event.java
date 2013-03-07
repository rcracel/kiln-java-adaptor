package com.nevermindsoft.kiln.internal.json;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * User: rcracel
 * Date: 3/6/13
 * Time: 3:11 PM
 */
public class Event {

    @SerializedName("module_name")
    private String moduleName;
    @SerializedName("log_level")
    private String logLevel;
    @SerializedName("message")
    private String message;
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("thread_name")
    private String threadName;
    @SerializedName("environment_name")
    private String environmentName;
    @SerializedName("platform")
    private String platform;
    @SerializedName("stack_trace")
    private String stackTrace;
    @SerializedName("source")
    private String source;

    @SerializedName("metadata")
    private Map<String, String> metadata;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
