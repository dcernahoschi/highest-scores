package com.dragos.highestscores.processing;

import java.util.Map;

/**
 * Created by dragos on 21.07.2018.
 */
public class Request {

    private RequestType requestType;
    private Map<String, ?> properties;

    public Request(RequestType requestType, Map<String, ?> properties) {

        if (requestType == null)
            throw new IllegalArgumentException("Null request type not allowed.");

        if (properties == null)
            throw new IllegalArgumentException("Null properties not allowed.");

        this.requestType = requestType;
        this.properties = properties;
    }

    public RequestType getRequestType() {

        return requestType;
    }

    public Object getPropertyValue(String propertyName) {

        return properties.get(propertyName);
    }
}
