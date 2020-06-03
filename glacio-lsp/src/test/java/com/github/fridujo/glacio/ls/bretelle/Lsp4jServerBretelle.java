package com.github.fridujo.glacio.ls.bretelle;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.json.MessageJsonHandler;
import org.eclipse.lsp4j.jsonrpc.json.StreamMessageProducer;
import org.eclipse.lsp4j.jsonrpc.messages.Message;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;

import com.github.fridujo.glacio.ls.launcher.CondensedMessageTracer;
import com.github.fridujo.glacio.ls.launcher.MessageProducerAwareLauncherBuilder;

/**
 * Bretelle for testing an implementation of {@link LanguageServer}.
 */
public class Lsp4jServerBretelle<T extends LanguageServer> implements Closeable {

    private static final MessageJsonHandler messageDebugSerializer = new MessageJsonHandler(Collections.emptyMap());

    private final LanguageServer languageServer;
    private final ServerSocket serverSocket;
    private final LanguageServer localProxyToRemoteServer;
    private final CloseableResources closeableResources;

    public <T extends LanguageServer> Lsp4jServerBretelle(T languageServer,
                                                          ServerSocket serverSocket,
                                                          LanguageServer localProxyToRemoteServer,
                                                          CloseableResources closeableResources) {
        this.languageServer = languageServer;
        this.serverSocket = serverSocket;
        this.localProxyToRemoteServer = localProxyToRemoteServer;
        this.closeableResources = closeableResources;
    }

    public static <T extends LanguageServer> Builder<T> builder(T languageServer) {
        return new Builder<T>(languageServer);
    }

    public static String messageToString(Message message) {
        return messageDebugSerializer.getGson().toJson(message);
    }

    public LanguageServer getLocalProxyToRemoteServer() {
        return localProxyToRemoteServer;
    }

    @Override
    public void close() {
        closeableResources.close();
    }

    public enum Side {
        SERVER, CLIENT;
    }

    public enum Way {
        REQUEST, RESPONSE
    }

    public static class Builder<T extends LanguageServer> {
        private final T languageServer;
        private int port = 0;

        public Builder(T languageServer) {
            this.languageServer = languageServer;
        }

        private static ServerSocket createServerSocket(int port) {
            ServerSocket serverSocket;
            try {
                serverSocket = new ServerSocket(port, 2, InetAddress.getLocalHost());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return serverSocket;
        }

        public Lsp4jServerBretelle build() {
            ExecutorService threadPool = Executors.newFixedThreadPool(2);
            ServerSocket serverSocket = createServerSocket(port);

            Semaphore serverHaveClient = new Semaphore(-1);

            threadPool.submit(ThrowingRunnable.silence(() -> {
                Socket socketServerSide = serverSocket.accept();

                Launcher<LanguageClient> serverLauncher = new MessageProducerAwareLauncherBuilder<LanguageClient>()
                    .setLocalService(languageServer)
                    .setRemoteInterface(LanguageClient.class)
                    .setInput(socketServerSide.getInputStream())
                    .setOutput(socketServerSide.getOutputStream())
                    .wrapMessages(new CondensedMessageTracer(System.out))
                    .create();

                serverLauncher.startListening();
                if (languageServer instanceof LanguageClientAware) {
                    ((LanguageClientAware) languageServer).connect(serverLauncher.getRemoteProxy());
                }

                serverHaveClient.release();
            }));

            LanguageClient client = new GenericLanguageClient();

            AtomicReference<Socket> socket = new AtomicReference<>();
            AtomicReference<LanguageServer> localProxyToRemoteServer = new AtomicReference<>();
            AtomicReference<StreamMessageProducer> streamMessageProducer = new AtomicReference<>();

            threadPool.submit(ThrowingRunnable.silence(() -> {
                socket.set(new Socket(InetAddress.getLocalHost(), serverSocket.getLocalPort()));

                MessageProducerAwareLauncherBuilder<LanguageServer> languageServerBuilder = (MessageProducerAwareLauncherBuilder<LanguageServer>) new MessageProducerAwareLauncherBuilder<LanguageServer>()
                    .setLocalService(client)
                    .setRemoteInterface(LanguageServer.class)
                    .setInput(socket.get().getInputStream())
                    .setOutput(socket.get().getOutputStream())
                    .wrapMessages(new CondensedMessageTracer(System.out));

                Launcher<LanguageServer> clientLauncher = languageServerBuilder.create();

                streamMessageProducer.set(languageServerBuilder.getMessageProducer());

                clientLauncher.startListening();
                localProxyToRemoteServer.set(clientLauncher.getRemoteProxy());
                serverHaveClient.release();
            }));

            try {
                serverHaveClient.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }

            return new Lsp4jServerBretelle(languageServer, serverSocket, localProxyToRemoteServer.get(), new CloseableResources(Arrays.asList(
                () -> Optional.ofNullable(streamMessageProducer.get()).ifPresent(StreamMessageProducer::close),
                () -> socket.get().close(),
                () -> serverSocket.close(),
                () -> threadPool.shutdownNow()
            )));
        }
    }
}
