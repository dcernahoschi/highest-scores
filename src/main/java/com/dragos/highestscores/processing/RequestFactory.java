package com.dragos.highestscores.processing;

import java.util.HashMap;
import java.util.Map;

import static com.dragos.highestscores.processing.PropertyNames.*;

/**
 * Created by dragos on 21.07.2018.
 */
public class RequestFactory {

    public static Request newLoginRequest(int userId) {

        Map<String, Object> properties = new HashMap<>();
        properties.put(PROPERTY_NAME_userId, userId);

        return new Request(RequestType.LOGIN, properties);
    }

    public static Request newSubmitScoreRequest(String sessionKey, int level, int score) {

        Map<String, Object> properties = new HashMap<>();
        properties.put(PROPERTY_NAME_sessionKey, sessionKey);
        properties.put(PROPERTY_NAME_level, level);
        properties.put(PROPERTY_NAME_score, score);

        return new Request(RequestType.SUBMIT_LEVEL_SCORE, properties);
    }

    public static Request newGetBestScoresRequest(int level) {

        Map<String, Object> properties = new HashMap<>();
        properties.put(PROPERTY_NAME_level, level);

        return new Request(RequestType.GET_BEST_SCORES, properties);
    }
}
