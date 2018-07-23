package com.dragos.highestscores.communication;

import com.dragos.highestscores.processing.*;
import com.dragos.highestscores.util.IOUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dragos on 22.07.2018.
 */
public class RestHttpHandler implements HttpHandler {

    private static final Pattern LOGIN_PATTERN = Pattern.compile("/(\\d{1,9})/login");
    private static final Pattern SUBMIT_LEVEL_PATTERN = Pattern.compile("/(\\d{1,9})/score\\?sessionKey=([0-9a-zA-Z]{1,})");
    private static final Pattern GET_BEST_SCORE_PATTERN = Pattern.compile("/(\\d{1,9})/highscorelist");

    private static final int HTTP_OK = 200;
    private static final int HTTP_BAD_REQUEST = 403;

    private Logger logger = Logger.getLogger(getClass().getName());

    private RequestProcessor requestProcessor;

    public RestHttpHandler(RequestProcessor requestProcessor) {

        this.requestProcessor = requestProcessor;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        URI requestURI = httpExchange.getRequestURI();

        logger.info(requestURI.toString());

        Matcher matcher1 = LOGIN_PATTERN.matcher(requestURI.toString());
        if (matcher1.matches()) {
            handleLogin(httpExchange, matcher1);
            return;
        }

        Matcher matcher2 = SUBMIT_LEVEL_PATTERN.matcher(requestURI.toString());
        if (matcher2.matches()) {
            handleScoreSubmit(httpExchange, matcher2);
            return;
        }

        Matcher matcher3 = GET_BEST_SCORE_PATTERN.matcher(requestURI.toString());
        if (matcher3.matches()) {
            handleGetBestScores(httpExchange, matcher3);
            return;
        }
    }

    private void handleGetBestScores(HttpExchange httpExchange, Matcher matcher) throws IOException {

        String level = matcher.group(1);
        if (level == null) {
            sendBadRequestResponse(httpExchange, "Null level not allowed");
            return;
        }
        logger.info("level: " + level);

        Request request = RequestFactory.newGetBestScoresRequest(Integer.valueOf(level));
        requestProcessor.process(request,
                response -> {

                    if (response.getResponseType() == ResponseType.FAIL) {
                        sendBadRequestResponse(httpExchange, response);
                        return;
                    }

                    Map<String, ?> properties = response.getProperties();
                    String bestScores = (String) properties.get(PropertyNames.PROPERTY_NAME_bestScores);

                    sendOkResponse(httpExchange, bestScores);
                });
    }

    private void handleScoreSubmit(HttpExchange httpExchange, Matcher matcher) throws IOException {

        String level = matcher.group(1);
        if (level == null) {
            sendBadRequestResponse(httpExchange, "Null level not allowed");
            return;
        }
        logger.info("level: " + level);

        String sessionKey = matcher.group(2);
        if (sessionKey == null) {
            sendBadRequestResponse(httpExchange, "Null session key not allowed");
            return;
        }
        logger.info("sessionKey: " + level);

        String score = IOUtils.readFromStream(httpExchange.getRequestBody());
        if (score == null) {
            sendBadRequestResponse(httpExchange, "Null score not allowed");
            return;
        }
        logger.info("score: " + score);

        Request request = RequestFactory.newSubmitScoreRequest(sessionKey, Integer.valueOf(level), Integer.valueOf(score));
        requestProcessor.process(request,
                response -> {
                    if (response.getResponseType() == ResponseType.FAIL) {
                        sendBadRequestResponse(httpExchange, response);
                        return;
                    }

                    sendOkResponse(httpExchange, "OK");
                });
    }

    private void handleLogin(HttpExchange httpExchange, Matcher matcher) throws IOException {

        String userId = matcher.group(1);
        if (userId == null) {
            sendBadRequestResponse(httpExchange, "Null user id not allowed");
            return;
        }
        logger.info("user id: " + userId);

        Request request = RequestFactory.newLoginRequest(Integer.valueOf(userId));
        requestProcessor.process(request,

                response -> {
                    ResponseType responseType = response.getResponseType();
                    if (responseType == ResponseType.FAIL) {
                        sendBadRequestResponse(httpExchange, response);
                        return;
                    }

                    Map<String, ?> properties = response.getProperties();
                    String sessionKey = (String) properties.get(PropertyNames.PROPERTY_NAME_sessionKey);

                    sendOkResponse(httpExchange, sessionKey);
                });
    }

    private void sendOkResponse(HttpExchange httpExchange, String responseBody) {

        try {
            httpExchange.sendResponseHeaders(HTTP_OK, responseBody.getBytes().length);
            IOUtils.writeToStream(httpExchange.getResponseBody(), responseBody);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unexpected IOEx while responding", e);
        }
    }

    private void sendBadRequestResponse(HttpExchange httpExchange, Response response) {

        Map<String, ?> properties = response.getProperties();
        String failureReason = (String) properties.get(PropertyNames.PROPERTY_NAME_requestFailureReason);
        sendBadRequestResponse(httpExchange, failureReason);
    }

    private void sendBadRequestResponse(HttpExchange httpExchange, String failureReason) {

        try {
            httpExchange.sendResponseHeaders(HTTP_BAD_REQUEST, failureReason.getBytes().length);
            IOUtils.writeToStream(httpExchange.getResponseBody(), failureReason);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unexpected IOEx while responding", e);
        }
    }
}
