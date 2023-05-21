package org.aujee.com.search_engine;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

class TomcatServer {
    private static final String HOST_NAME = "localhost";
    private static ServiceExecutorCustomizer serviceExecutor;
    private static EndpointExecutorCustomizer endpointExecutor;
    private static Tomcat tomcat;
    private static Server server;
    private static Service service;
    private static Engine engine;
    private static Connector[] webConnectors;
    private static Context context;
    private Path keystoreFile;
    private String keystorePassword;
    private String keyAlias;

    public static void start(ExecutorType executorType, int utilityThreads) throws IOException {
        TomcatServer tomcatServer = new TomcatServer(executorType);
        configureServer(server, utilityThreads);
        configureService(service, executorType);
        configureEngine(engine);
        configureContext(context);
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    public static void await() {
        server.await();
    }

    private TomcatServer (ExecutorType type) {
        endpointExecutor = EndpointExecutorCustomizer.withExecutor(type);
        serviceExecutor = ServiceExecutorCustomizer.withExecutor(type);
        tomcat = new Tomcat();
        server = tomcat.getServer();
        service = tomcat.getService();
        engine = tomcat.getEngine();
        webConnectors = createConnectors(endpointExecutor);
        context = tomcat.addContext("", (new File(".")).getAbsolutePath());
    }

    private static void configureServer (Server server, int utilityThreads) {
        server.setUtilityThreads(utilityThreads);
    }

    private static void configureService (Service service, ExecutorType type) {
        service.addExecutor(serviceExecutor);
        for (Connector connector : webConnectors) {
            service.addConnector(connector);
        }
    }

    private static void configureEngine (Engine engine) {
        engine.setDefaultHost(HOST_NAME);
    }

    private static void configureContext (Context context) {
        context.addLifecycleListener(new ContextConfig());
        WebResourceRoot root = new StandardRoot(context);
        URL url = findClassLocation(Main.class);
        root.createWebResourceSet(WebResourceRoot.ResourceSetType.PRE, "/WEB-INF/classes", url, "/");
        context.setResources(root);
    }

    private synchronized Connector[] createConnectors(Executor executor) {
        class SSLHostConfigurer {
            final SSLHostConfig sslHostConfig = new SSLHostConfig();
            final SSLHostConfigCertificate cert = new SSLHostConfigCertificate(
                    sslHostConfig, SSLHostConfigCertificate.Type.RSA);

            SSLHostConfig provide() {
                keystoreFile = Path.of(System.getProperty("user.home") + "/selfsigned.jks");
                keyAlias = "tomcat";
                keystorePassword = "zabka98";
                cert.setCertificateKeystoreFile(keystoreFile.toAbsolutePath().toString());
                cert.setCertificateKeyAlias(keyAlias);
                cert.setCertificateKeystorePassword(keystorePassword);
                sslHostConfig.addCertificate(cert);
                return sslHostConfig;
            }
        }

        List<Connector> connectors = new ArrayList<>();

        SSLHostConfig sslHostConfig = new SSLHostConfigurer().provide();
        Connector secureConnector = getConnector(executor);
        secureConnector.setPort(8443);
        secureConnector.setSecure(true);
        secureConnector.setScheme("https");
        secureConnector.setProperty("SSLEnabled", "true");
        secureConnector.addSslHostConfig(sslHostConfig);

        connectors.add(secureConnector);

        Connector inSecureConnector = getConnector(executor);
        inSecureConnector.setPort(8080);
        inSecureConnector.setSecure(false);
        inSecureConnector.setScheme("http");

        connectors.add(inSecureConnector);

        connectors.forEach (connector -> {
            connector.setXpoweredBy(false);
            connector.setProperty("acceptCount", "100");
            connector.setProperty("compression", "on");
            //set in servlet container!!!
            connector.setProperty("disableUploadTimeout", "true");
            connector.setProperty("socket.performanceConnectionTime", "1");
            connector.setProperty("socket.performanceLatency", "0");
            connector.setProperty("socket.performanceBandwidth", "2");
            connector.setEncodedSolidusHandling("passthrough");
            connector.setProperty("maxHttpHeaderSize", "16384");
            connector.setProperty("maxKeepAliveRequests", "100");
            connector.setProperty("useKeepAliveResponseHeader", "false");
            connector.setProperty("maxConnections", "10000");
            connector.setProperty("processorCache", "10000");
            connector.setProperty("socket.soReuseAddress", "true");
            //        connector.setRejectSuspiciousURIs(true);
            //        connector.setProperty("connectionTimeout", Duration.ofSeconds(1L).toString());
            //        connector.setProperty("keepAliveTimeout", Duration.ofSeconds(2L).toString());
            //        connector.setProperty("socket.soLingerOn", "true");
            //        connector.setProperty("socket.soLingerTime", "0");
        });

        return connectors.toArray(new Connector[0]);
    }

    private Connector getConnector(Executor executor) {
        Connector connector = new Connector(Http11Nio2Protocol.class.getName());
        connector.addUpgradeProtocol(new Http2Protocol());
        connector.getProtocolHandler().setExecutor(executor);
        return connector;
    }

    private static URL findClassLocation(Class<?> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation();
    }
}

