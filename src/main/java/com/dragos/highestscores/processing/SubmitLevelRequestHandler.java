package com.dragos.highestscores.processing;

import java.util.HashMap;

/**
 * Created by dragos on 21.07.2018.
 */
public class SubmitLevelRequestHandler implements RequestHandler {

    @Override
    public Response handle(Request request, ProcessingState processingState) {

        String sessionKey = (String) request.getPropertyValue(PropertyNames.PROPERTY_NAME_sessionKey);
        if (sessionKey == null)
            return ResponseFactory.newFailedResponse(request, "No session provided");

        SessionManager sessionManager = processingState.getSessionManager();
        Integer userId = sessionManager.getUserIdWithSessionKey(sessionKey);
        if (userId == null)
            return ResponseFactory.newFailedResponse(request, "Session key invalid");

        sessionManager.renewSession(sessionKey);

        Integer level = (Integer) request.getPropertyValue(PropertyNames.PROPERTY_NAME_level);
        Integer score = (Integer) request.getPropertyValue(PropertyNames.PROPERTY_NAME_score);

        BusinessOperationManager businessOperationManager = processingState.getBusinessOperationManager();
        businessOperationManager.addScoreForUserOnLevel(score, userId, level);

        return ResponseFactory.newSuccessResponse(request, new HashMap<>());
    }
}
