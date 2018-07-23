package com.dragos.highestscores.processing;

import java.util.HashMap;
import java.util.Map;

import static com.dragos.highestscores.processing.PropertyNames.PROPERTY_NAME_requestFailureReason;

/**
 * Created by dragos on 21.07.2018.
 */
public class ResponseFactory {

    public static Response newFailedResponse(Request request, String failureReason) {

        Map<String, Object> properties = new HashMap<>();
        properties.put(PROPERTY_NAME_requestFailureReason, failureReason);
        return new Response(ResponseType.FAIL, request, properties);
    }

    public static Response newSuccessResponse(Request request, Map<String, ?> properties) {

        return new Response(ResponseType.SUCCESS, request, properties);
    }
}
