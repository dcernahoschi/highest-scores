package com.dragos.highestscores.processing;

import java.util.HashMap;
import java.util.Map;

import static com.dragos.highestscores.processing.PropertyNames.PROPERTY_NAME_sessionKey;
import static com.dragos.highestscores.processing.PropertyNames.PROPERTY_NAME_userId;

/**
 * Created by dragos on 21.07.2018.
 */
public class LoginRequestHandler implements RequestHandler {

    @Override
    public Response handle(Request request, ProcessingState processingState) {

        Integer userId = (Integer) request.getPropertyValue(PROPERTY_NAME_userId);
        if (userId == null)
            return ResponseFactory.newFailedResponse(request, "No user id provided");

        SessionManager sessionManager = processingState.getSessionManager();
        String sessionKey = sessionManager.createNewSession(userId);

        Map<String, Object> properties = new HashMap<>();
        properties.put(PROPERTY_NAME_sessionKey, sessionKey);
        return ResponseFactory.newSuccessResponse(request, properties);
    }
}
