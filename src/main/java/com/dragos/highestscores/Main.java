package com.dragos.highestscores;

import com.dragos.highestscores.communication.RestEntryPoint;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dragos on 23.07.2018.
 */
public class Main {

    private static Logger logger = Logger.getLogger(RestEntryPoint.class.getName());

    public static void main(String[] args) {

        final RestEntryPoint restEntryPoint = new RestEntryPoint();

        try {
            restEntryPoint.start();
        }
        catch (Throwable t) {
            logger.log(Level.WARNING, "Unexpected exception during start", t);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

                try {
                    restEntryPoint.stop();
                } catch (Throwable e) {
                    logger.log(Level.WARNING, "Unexpected exception during stop", e);
                }
            }
        });
    }
}
