package com.dragos.highestscores.processing;

import com.dragos.highestscores.util.ExpiringMap;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by dragos on 21.07.2018.
 */
public class SessionManager {

    private static final long SESSION_TIMEOUT_MS = 900_000;

    private static final AtomicLong sessionKeyGenerator = new AtomicLong();

    private ExpiringMap<String, Integer> sessionKeyToUserId; //assume one session per user

    public SessionManager() {

        sessionKeyToUserId = new ExpiringMap<>(SESSION_TIMEOUT_MS);
        sessionKeyToUserId.addListener((key, value) -> sessionKeyToUserId.remove(key));
    }

    public String createNewSession(Integer userId) {

        String sessionKey = String.valueOf(sessionKeyGenerator.incrementAndGet());
        sessionKeyToUserId.put(sessionKey, userId);
        return sessionKey;
    }

    public void renewSession(String sessionKey) {

        Integer userId = sessionKeyToUserId.get(sessionKey);
        sessionKeyToUserId.put(sessionKey, userId);
    }

    public Integer getUserIdWithSessionKey(String sessionKey) {

        return sessionKeyToUserId.get(sessionKey);
    }
}
