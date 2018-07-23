package com.dragos.highestscores.processing;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Created by dragos on 23.07.2018.
 */
public class BestScoresTableTest {

    @Test
    public void testSingleUserScore() {

        int userId = 100;
        int score1 = 1000;
        int score2 = 2000;

        BestScoresTable bestScoresTable = new BestScoresTable();

        bestScoresTable.addScore(userId, score1);

        Assert.assertEquals(1, bestScoresTable.getScores().size());

        bestScoresTable.addScore(userId, score2);

        Assert.assertEquals(1, bestScoresTable.getScores().size());

        SortedMap<Integer, Integer> scoresMap = bestScoresTable.getScores();
        Assert.assertTrue(scoresMap.containsKey(score2));
    }

    @Test
    public void testMultiUserScores() {

        int userId1 = 100;
        int userId2 = 200;
        int userId3 = 300;

        int score1 = 1000;
        int score2 = 2000;
        int score3 = 3000;

        BestScoresTable bestScoresTable = new BestScoresTable();

        bestScoresTable.addScore(userId1, score1);
        bestScoresTable.addScore(userId2, score2);
        bestScoresTable.addScore(userId3, score3);

        SortedMap<Integer, Integer> scores = bestScoresTable.getScores();
        Assert.assertEquals(3, scores.size());

        Assert.assertEquals(score3, (int) scores.firstKey());
        Assert.assertEquals(score1, (int) scores.lastKey());
    }

    @Test
    public void testTableWithMaxNumberOfScores() {

        int userId1 = 100;
        int userId2 = 200;
        int userId3 = 300;
        int userId4 = 400;

        int score1 = 1000;
        int score2 = 2000;
        int score3 = 3000;
        int score4 = 4000;

        BestScoresTable bestScoresTable = new BestScoresTable();
        bestScoresTable.setMaxNumberOfScores(3);

        bestScoresTable.addScore(userId1, score1);
        bestScoresTable.addScore(userId2, score2);
        bestScoresTable.addScore(userId3, score3);
        bestScoresTable.addScore(userId4, score4);

        SortedMap<Integer, Integer> scores = bestScoresTable.getScores();
        Assert.assertEquals(3, scores.size());

        Assert.assertEquals(score4, (int) scores.firstKey());
        Assert.assertEquals(score2, (int) scores.lastKey());
    }

    @Test
    public void testTableWithMaxNumberOfScoresAndScoreFromAnExistingUser() {

        int userId1 = 100;
        int userId2 = 200;
        int userId3 = 300;

        int score1 = 1000;
        int score2 = 2000;
        int score3 = 3000;
        int score4 = 4000;

        BestScoresTable bestScoresTable = new BestScoresTable();
        bestScoresTable.setMaxNumberOfScores(3);

        bestScoresTable.addScore(userId1, score1);
        bestScoresTable.addScore(userId2, score2);
        bestScoresTable.addScore(userId3, score3);
        bestScoresTable.addScore(userId3, score4);

        SortedMap<Integer, Integer> scores = bestScoresTable.getScores();
        Assert.assertEquals(3, scores.size());

        Assert.assertEquals(score4, (int) scores.firstKey());
        Assert.assertEquals(score1, (int) scores.lastKey());
    }
}
