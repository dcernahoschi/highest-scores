package com.dragos.highestscores.processing;

import com.dragos.highestscores.util.ExpiringMap;

import java.util.Map;

/**
 * Created by dragos on 21.07.2018.
 */
public class ProcessingState {

    private SessionManager sessionManager;
    private BusinessOperationManager businessOperationManager;

    public ProcessingState() {

        this.sessionManager = new SessionManager();
        this.businessOperationManager = new BusinessOperationManager();
    }

    public SessionManager getSessionManager() {

        return sessionManager;
    }

    public BusinessOperationManager getBusinessOperationManager() {

        return businessOperationManager;
    }
}
