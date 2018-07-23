package com.dragos.highestscores.communication;

import com.dragos.highestscores.processing.AsyncRequestProcessor;
import com.dragos.highestscores.util.NamedThreadFactory;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dragos on 21.07.2018.
 */
public class RestEntryPoint {

    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 8081;
    private static final int QUEUE_LENGTH = 10000;
    private static InetSocketAddress ADDRESS = new InetSocketAddress(HOSTNAME, PORT);

    private static final int DEFAULT_WORKER_THREADS = 10;

    private ExecutorService httpService;
    private HttpServer httpServer;

    private AsyncRequestProcessor requestProcessor;

    public void start() throws Exception {

        requestProcessor = new AsyncRequestProcessor();
        requestProcessor.init();

        httpService = Executors.newFixedThreadPool(DEFAULT_WORKER_THREADS, new NamedThreadFactory(getClass().getSimpleName() + "-HttpWorkerThread"));

        httpServer = HttpServer.create(ADDRESS, QUEUE_LENGTH);
        httpServer.createContext("/", new RestHttpHandler(requestProcessor));
        httpServer.setExecutor(httpService);
        httpServer.start();
    }

    public void stop() {

        httpServer.stop(0);
        httpService.shutdownNow();

        requestProcessor.close();
    }
}
