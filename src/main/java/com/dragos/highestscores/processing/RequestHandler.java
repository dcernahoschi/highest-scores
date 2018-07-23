package com.dragos.highestscores.processing;

/**
 * Created by dragos on 21.07.2018.
 */
public interface RequestHandler {

    Response handle(Request request, ProcessingState processingState);
}
