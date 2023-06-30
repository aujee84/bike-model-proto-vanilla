package org.aujee.com.searchengine;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.StandardThreadExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class ServiceExecutorCustomizer implements  org.apache.catalina.Executor {

    private static final String executorPlaceHolder = "ofService-";
    private String executorName;
    private Executor executor;

    private ServiceExecutorCustomizer(ExecutorType type) {
        switch (type) {
            case VIRTUAL -> {
                executorName = "Virtual-";
                executor = Executors.newThreadPerTaskExecutor(
                        Thread.ofVirtual()
                                .name(executorPlaceHolder + executorName + "thread-", 1)
                                .factory());
            }
            case DEFAULT -> {
                executorName = "Default-";
                executor = new StandardThreadExecutor();
            }
        }
    }

    public static ServiceExecutorCustomizer withExecutor(ExecutorType type) {
        return new ServiceExecutorCustomizer(type);
    }

    @Override
    public String getName() {
        return executorPlaceHolder + executorName;
    }

    @Override
    public void execute(final Runnable command) {
        executor.execute(command);
    }

    @Override
    public void addLifecycleListener(final LifecycleListener listener) {
    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(final LifecycleListener listener) {
    }

    @Override
    public void init() throws LifecycleException {
    }

    @Override
    public void start() throws LifecycleException {
    }

    @Override
    public void stop() throws LifecycleException {
    }

    @Override
    public void destroy() throws LifecycleException {
    }

    @Override
    public LifecycleState getState() {
        return null;
    }

    @Override
    public String getStateName() {
        return null;
    }
}
