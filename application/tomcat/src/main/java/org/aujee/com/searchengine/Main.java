package org.aujee.com.searchengine;

import org.aujee.com.shared.api.handlers.AutoInitializer;

import java.io.IOException;

import static org.aujee.com.searchengine.ExecutorType.VIRTUAL;

public class Main {
    public static void main(String[] args) throws IOException {

        AutoInitializer.ConfigurationHandler.initialize();

        TomcatServer.start(VIRTUAL, 1);
        TomcatServer.await();
    }
}
