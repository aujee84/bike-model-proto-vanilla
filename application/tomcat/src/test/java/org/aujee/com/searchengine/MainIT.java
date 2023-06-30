package org.aujee.com.searchengine;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

class MainIT {

    @Test
    void testServerStartsAndStops() throws IOException, InterruptedException {
        TomcatServer.start(ExecutorType.VIRTUAL,1);
        Thread.sleep(Duration.ofSeconds(3));
        TomcatServer.stop();
    }
}
