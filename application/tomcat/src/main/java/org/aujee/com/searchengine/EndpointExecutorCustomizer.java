package org.aujee.com.searchengine;

import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThreadFactory;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class EndpointExecutorCustomizer implements Executor {

    private final String executorPlaceHolder = "ofEndpoint-";
    private String executorName;
    private Executor executor;

    private EndpointExecutorCustomizer(ExecutorType type) {

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
                executor = createDefaultExecutor();
            }
        }
    }

    public static EndpointExecutorCustomizer withExecutor(ExecutorType type) {
        return new EndpointExecutorCustomizer(type);
    }

    private Executor createDefaultExecutor() {
        TaskQueue taskqueue = new TaskQueue();
        TaskThreadFactory tf = new TaskThreadFactory(executorPlaceHolder + executorName + "exec-", true, Thread.NORM_PRIORITY);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                            1,
                            200,
                            60,
                            TimeUnit.SECONDS,
                            taskqueue,
                            tf);
        taskqueue.setParent((ThreadPoolExecutor) executor);
        return executor;
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }
}
