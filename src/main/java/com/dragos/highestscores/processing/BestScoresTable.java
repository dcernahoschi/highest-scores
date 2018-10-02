package com.dragos.highestscores.processing;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by dragos on 21.07.2018.
 */
public class BestScoresTable {

    private static final int DEFAULT_MAX_NUMBER_OF_SCORES = 15;

    private final SortedMap<Integer, Integer> scoreToUserId = new TreeMap<>((o1, o2) -> -Integer.compare(o1, o2));
    private final Map<Integer, Integer> userIdToScore = new HashMap<>();

    private volatile int minimumScore = Integer.MIN_VALUE;
    private volatile boolean tableFull = false;

    private int maxNumberOfScores = DEFAULT_MAX_NUMBER_OF_SCORES;

    public void addScore(int userId, int score) {

        if (!tableFull) {
            synchronized (this) {
                if (!tableFull) {
                    removeUserPreviousScoreIfExists(userId, score);
                    addUserCurrentScore(userId, score);
                }
                if (!tableFull && scoreToUserId.size() == maxNumberOfScores)
                    tableFull = true;
                return;
            }
        }

        if (score < minimumScore)
            return;

        synchronized (this) {
            boolean removed = removeUserPreviousScoreIfExists(userId, score);
            if (!removed)
                scoreToUserId.remove(minimumScore);
            addUserCurrentScore(userId, score);
        }
    }

    private boolean removeUserPreviousScoreIfExists(int userId, int score) {

        Integer userPreviousScore = userIdToScore.get(userId);
        if (userPreviousScore != null && userPreviousScore < score) {
            userIdToScore.remove(userId);
            scoreToUserId.remove(userPreviousScore);
            return true;
        }
        return false;
    }

    private void addUserCurrentScore(int userId, int score) {

        scoreToUserId.put(score, userId);
        userIdToScore.put(userId, score);
        minimumScore = scoreToUserId.lastKey();
    }

    public SortedMap<Integer, Integer> getScores() {

        synchronized (this) {
            return new TreeMap<>(scoreToUserId);
        }
    }

    public void setMaxNumberOfScores(int maxNumberOfScores) {

        this.maxNumberOfScores = maxNumberOfScores;
    }
}
