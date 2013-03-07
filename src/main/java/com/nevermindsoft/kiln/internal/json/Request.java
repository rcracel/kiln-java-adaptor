package com.nevermindsoft.kiln.internal.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * User: rcracel
 * Date: 3/6/13
 * Time: 7:31 PM
 */
public class Request {

    @SerializedName("events")
    List<Event> events;

    @SerializedName("api_key")
    String apiKey;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
