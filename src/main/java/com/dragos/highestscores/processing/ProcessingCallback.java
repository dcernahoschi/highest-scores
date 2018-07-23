package com.dragos.highestscores.processing;

import java.io.IOException;

/**
 * Created by dragos on 21.07.2018.
 */
public interface ProcessingCallback {

    void requestProcessed(Response response);
}
