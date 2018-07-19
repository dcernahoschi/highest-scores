package com.dragos.util;

import java.util.concurrent.ThreadFactory;

/**
 * Created by dragos on 19.07.2018.
 */
public class NamedThreadFactory implements ThreadFactory {

    private ThreadGroup threadGroup = null;

    private int newThreadCount = 0;

    public NamedThreadFactory(String groupName) {

        threadGroup = new ThreadGroup(Thread.currentThread().getThreadGroup(), groupName);
    }

    public Thread newThread(Runnable runnable) {

        Thread thread = new Thread(threadGroup, runnable);
        synchronized (this) {
            thread.setName(createThreadName());
        }

        return thread;
    }

    protected synchronized String createThreadName() {

        return threadGroup.getName() + "-" + ++newThreadCount;
    }
}
