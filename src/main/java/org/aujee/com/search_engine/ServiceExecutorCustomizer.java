package org.aujee.com.search_engine;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.StandardThreadExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class CustomExecutors implements  org.apache.catalina.Executor {

    private String executorName;
    private ExecutorService executorService;

    private CustomExecutors(ExecutorType type) {
        switch (type) {
            case VIRTUAL -> {
                executorName = "ofVirtual-";
                executorService = Executors.newThreadPerTaskExecutor(
                        Thread.ofVirtual()
                                .name(executorName + "thread-", 1)
                                .factory());
            }
            case DEFAULT -> {
                executorName = "default-";
                executorService = (ExecutorService) new StandardThreadExecutor();
            }
        }
    }

    public static CustomExecutors withExecutor(ExecutorType type) {
        return new CustomExecutors(type);
    }

    private volatile LifecycleState state = LifecycleState.NEW;

    @Override
    public String getName() {
        return executorName;
    }

    @Override
    public void execute(final Runnable command) {
        executorService.submit(command);
    }

    @Override
    public void addLifecycleListener(final LifecycleListener listener) {
        lifecycleListeners.add(listener);
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
        if (LifecycleState.STARTING_PREP.equals(state) || LifecycleState.STARTING.equals(state) ||
                LifecycleState.STARTED.equals(state)) {

            if (log.isDebugEnabled()) {
                Exception e = new LifecycleException();
                log.debug(sm.getString("lifecycleBase.alreadyStarted", toString()), e);
            } else if (log.isInfoEnabled()) {
                log.info(sm.getString("lifecycleBase.alreadyStarted", toString()));
            }

            return;
        }
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
