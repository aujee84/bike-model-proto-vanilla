package org.aujee.com.searchengine;

import java.io.IOException;

import static org.aujee.com.searchengine.ExecutorType.VIRTUAL;

class Main {
    public static void main(String[] args) throws IOException {
        TomcatServer.start(VIRTUAL, 1);
        TomcatServer.await();
    }
}
