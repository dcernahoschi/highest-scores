package com.dragos.highestscores.processing;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dragos on 21.07.2018.
 */
public class GetBestScoresRequestHandler implements RequestHandler {

    @Override
    public Response handle(Request request, ProcessingState processingState) {

        Integer level = (Integer) request.getPropertyValue(PropertyNames.PROPERTY_NAME_level);
        if (level == null)
            return ResponseFactory.newFailedResponse(request, "No level provided");

        BusinessOperationManager businessOperationManager = processingState.getBusinessOperationManager();

        Map<String, Object> properties = new HashMap<>();
        properties.put(PropertyNames.PROPERTY_NAME_bestScores, businessOperationManager.getBestScores(level));
        return ResponseFactory.newSuccessResponse(request, properties);
    }
}
