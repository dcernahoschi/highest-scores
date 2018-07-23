package com.dragos.highestscores.processing;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dragos on 21.07.2018.
 */
public class Response {

    private ResponseType responseType;

    private Request request;
    private Map<String, ?> properties;

    Response(ResponseType responseType, Request request, Map<String, ?> properties) {

        if (responseType == null)
            throw new IllegalArgumentException("Null response type not allowed");

        if (request == null)
            throw new IllegalArgumentException("Null request not allowed");

        if (properties == null)
            properties = new HashMap<>();

        this.responseType = responseType;
        this.request = request;
        this.properties = properties;
    }

    public ResponseType getResponseType() {

        return responseType;
    }

    public Request getRequest() {

        return request;
    }

    public Map<String, ?> getProperties() {

        return properties;
    }
}
