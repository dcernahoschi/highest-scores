package com.dragos.highestscores.processing;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by dragos on 21.07.2018.
 */
public class BusinessOperationManager {

    private ConcurrentMap<Integer, BestScoresTable> levelToBestScores = new ConcurrentHashMap<>();

    public void addScoreForUserOnLevel(int score, int userId, int level) {

        BestScoresTable bestScoresTable = levelToBestScores.computeIfAbsent(level, k -> new BestScoresTable());
        bestScoresTable.addScore(userId, score);
    }

    public String getBestScores(int level) {

        BestScoresTable bestScoresTable = levelToBestScores.get(level);
        if (bestScoresTable == null)
            return "";

        SortedMap<Integer, Integer> scores = bestScoresTable.getScores();
        return scores.toString();
    }
}
