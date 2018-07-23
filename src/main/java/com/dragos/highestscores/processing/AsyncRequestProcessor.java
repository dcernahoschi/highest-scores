package com.dragos.highestscores.processing;

import com.dragos.highestscores.util.NamedThreadFactory;
import com.dragos.highestscores.util.Tuple;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dragos on 21.07.2018.
 */
public class AsyncRequestProcessor implements RequestProcessor {

    private static final int DEFAULT_CONCURRENCY = 4;
    private static final int MAX_PENDING_REQUESTS = 1_000_000;

    private Logger logger = Logger.getLogger(getClass().getName());

    private AtomicLong numberOfPendingRequests = new AtomicLong();
    private ConcurrentLinkedQueue<Tuple<Request, ProcessingCallback>> pendingRequests = new ConcurrentLinkedQueue<>();
    private ExecutorService processingService;

    private Map<RequestType, RequestHandler> requestTypeToRequestHandler;
    private ProcessingState processingState;

    public void init() {

        requestTypeToRequestHandler = new HashMap<>();
        requestTypeToRequestHandler.put(RequestType.LOGIN, new LoginRequestHandler());
        requestTypeToRequestHandler.put(RequestType.SUBMIT_LEVEL_SCORE, new SubmitLevelRequestHandler());
        requestTypeToRequestHandler.put(RequestType.GET_BEST_SCORES, new GetBestScoresRequestHandler());

        processingState = new ProcessingState();

        processingService = Executors.newFixedThreadPool(DEFAULT_CONCURRENCY, new NamedThreadFactory(getClass() + "-ProcessingServiceThread"));
        processingService.submit(new ProcessingRunnable());
    }

    public void close() {

        processingService.shutdownNow();
        pendingRequests.clear();
        numberOfPendingRequests.set(0);
        processingState = null;
    }

    @Override
    public void process(Request request, ProcessingCallback callback) {

        if (numberOfPendingRequests.incrementAndGet() >= MAX_PENDING_REQUESTS)
            callback.requestProcessed(ResponseFactory.newFailedResponse(request, "Server too busy"));
        pendingRequests.offer(new Tuple<>(request, callback));
    }

    private class ProcessingRunnable implements Runnable {

        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                Request currentProcessingRequest = null;

                try {
                    Tuple<Request, ProcessingCallback> tuple = pendingRequests.poll();
                    if (tuple == null) {
                        Thread.sleep(10);
                        continue;
                    }

                    Request request = tuple.getFirst();
                    currentProcessingRequest = request;
                    RequestHandler requestHandler = requestTypeToRequestHandler.get(request.getRequestType());
                    Response response = requestHandler.handle(request, processingState);

                    ProcessingCallback callback = tuple.getSecond();
                    callback.requestProcessed(response);

                    numberOfPendingRequests.decrementAndGet();
                } catch (InterruptedException e) {
                    logger.info("Stopped");
                } catch (Throwable t) {
                    logger.log(Level.WARNING, "Unexpected exception in processing of request " + currentProcessingRequest, t);
                }
            }
        }
    }
}
