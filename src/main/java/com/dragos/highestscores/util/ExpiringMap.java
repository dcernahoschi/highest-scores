package com.dragos.highestscores.util;

import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dragos on 19.07.2018.
 */
public class ExpiringMap<K,V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 4;

    private Logger logger = Logger.getLogger(getClass().getName());

    private long timeoutInMs;
    private ConcurrentMap<K, Tuple<Long, V>> _keyToValue = new ConcurrentHashMap<>(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);

    private ExecutorService expirationService = Executors.newSingleThreadExecutor(new NamedThreadFactory(getClass().getSimpleName() + "-ExpirationThread"));
    private ScheduledExecutorService expirationScheduler = Executors.newScheduledThreadPool(DEFAULT_CONCURRENCY_LEVEL, new NamedThreadFactory(getClass().getSimpleName() + "-ExpirationScheduler"));

    private Thread monitorThread;

    private List<ExpirationListener<K, V>> listeners;

    public ExpiringMap(long timeoutInMs) {

        if (timeoutInMs <= 0)
            throw new IllegalArgumentException("Timeout arg is < 0 " + timeoutInMs);

        this.timeoutInMs = timeoutInMs;

        listeners = new CopyOnWriteArrayList<>();

        monitorThread = new Thread(() -> {

            while (!Thread.currentThread().isInterrupted()) {

                logger.info("ExpiringMap size " + _keyToValue.size());

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        monitorThread.setName("ExpiringMap-Monitor-Thread");
        monitorThread.start();
    }

    public V put(K key, V value) {

        long insertTimeInMs = System.currentTimeMillis();

        Tuple<Long, V> insertTimeAndValue = _keyToValue.put(key, new Tuple<>(insertTimeInMs, value));

        scheduleExpiration(key, insertTimeInMs);

        if (insertTimeAndValue != null)
            return insertTimeAndValue.getSecond();
        return null;
    }

    private void scheduleExpiration(K key, long insertTimeInMs) {

        long originalExpirationTime = insertTimeInMs + timeoutInMs;

        Runnable expirationRunnable = () -> {

            try {
                Tuple<Long, V> insertTimeAndValue = _keyToValue.get(key);
                if (insertTimeAndValue == null) //already removed
                    return;
                long updatedExpirationTime = insertTimeAndValue.getFirst() + timeoutInMs;
                if (updatedExpirationTime == originalExpirationTime) //might have been updated
                    notifyListeners(key, insertTimeAndValue.getSecond());
            } catch (Throwable e) {
                logger.log(Level.WARNING, "Unexpected throwable while removing expired entry", e);
            }
        };

        expirationScheduler.schedule(expirationRunnable, timeoutInMs, TimeUnit.MILLISECONDS);
    }

    public V putIfAbsent(K key, V value) {

        long insertTimeInMs = System.currentTimeMillis();

        Tuple<Long, V> insertTimeAndValue = _keyToValue.putIfAbsent(key, new Tuple<>(insertTimeInMs, value));

        scheduleExpiration(key, insertTimeInMs);

        if (insertTimeAndValue != null)
            return insertTimeAndValue.getSecond();
        return null;
    }

    public V get(K key) {

        if (key == null)
            throw new IllegalArgumentException("Key is null");

        Tuple<Long, V> insertTimeAndValue = _keyToValue.get(key);
        if (insertTimeAndValue != null)
            return insertTimeAndValue.getSecond();

        return null;
    }

    public int size() {

        return _keyToValue.size();
    }

    public void clear() {

        _keyToValue.clear();
    }

    public void remove(K key) {

        _keyToValue.remove(key);
    }

    public void close() {

        logger.info("Expiring map on closing has entries " + _keyToValue.size());

        expirationScheduler.shutdownNow();
        expirationService.shutdownNow();
        monitorThread.interrupt();
    }

    private void notifyListeners(K key, V value) {

        listeners.forEach(listener -> {
            try {
                listener.entryExpired(key, value);
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Exception in listener ", t);
            }
        });
    }

    public void addListener(ExpirationListener<K, V> expirationListener) {

        listeners.add(expirationListener);
    }

    public static void main(String[] args) throws InterruptedException {

        ExpiringMap<String, Object> expiringMap = new ExpiringMap<>(10_000);
        expiringMap.put("k1", "v1");
        expiringMap.addListener((key, value) -> {
            System.out.println(key + " expired");
            expiringMap.remove(key);
        });

        Thread.sleep(100000);
    }
}
