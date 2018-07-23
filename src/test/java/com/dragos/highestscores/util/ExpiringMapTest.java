package com.dragos.highestscores.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dragos on 23.07.2018.
 */
public class ExpiringMapTest {

    @Test
    public void testSessionKeyExpireOnTimeout() throws InterruptedException {

        int userId = 100;
        String sessionKey = "123";

        ExpiringMap<String, Integer> expiringMap = new ExpiringMap<>(1_000);
        expiringMap.put(sessionKey, userId);
        expiringMap.addListener((key, value) -> expiringMap.remove(sessionKey));

        Thread.sleep(2_000);

        Assert.assertNull(expiringMap.get(sessionKey));
    }

    @Test
    public void testSessionKeyIsRenewed() throws InterruptedException {

        int userId = 100;
        String sessionKey = "123";

        ExpiringMap<String, Integer> expiringMap = new ExpiringMap<>(1_000);
        expiringMap.put(sessionKey, userId);
        expiringMap.addListener((key, value) -> expiringMap.remove(sessionKey));

        Thread.sleep(800);

        expiringMap.put(sessionKey, userId); //renew

        Thread.sleep(500);

        Assert.assertNotNull(expiringMap.get(sessionKey));
    }
}
