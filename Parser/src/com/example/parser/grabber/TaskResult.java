package com.example.parser.grabber;


import java.util.Set;

public class TaskResult {

    private final String baseUrl;
    private final Set<String> links;
    private final long timeSpendInMS;

    public TaskResult(String baseUrl, Set<String> links, long timeSpendInMS) {
        this.baseUrl = baseUrl;
        this.links = links;
        this.timeSpendInMS = timeSpendInMS;
    }

    public Set<String> getLinks() {
        return links;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public long getTimeSpendInMS() {
        return timeSpendInMS;
    }
}
