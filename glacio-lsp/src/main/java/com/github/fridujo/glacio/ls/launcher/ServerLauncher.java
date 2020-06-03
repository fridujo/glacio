package com.github.fridujo.glacio.ls.launcher;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;

import com.github.fridujo.glacio.ls.GlacioLanguageServer;

public class ServerLauncher {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        String localAddress = "127.0.0.1";
        int port = 7121;
        ServerSocket serverSocket = new ServerSocket(port, 1, InetAddress.getByName(localAddress));
        System.out.println("Server listening on: " + localAddress + ":" + port);

        for (; ; ) {
            Socket socketServerSide = serverSocket.accept();

            GlacioLanguageServer glacioLanguageServer = new GlacioLanguageServer();

            System.out.println("Got socket");
            MessageProducerAwareLauncherBuilder<LanguageClient> clientLauncherBuilder = (MessageProducerAwareLauncherBuilder<LanguageClient>) new MessageProducerAwareLauncherBuilder<LanguageClient>()
                .setLocalService(glacioLanguageServer)
                .setRemoteInterface(LanguageClient.class)
                .setInput(socketServerSide.getInputStream())
                .setOutput(socketServerSide.getOutputStream())
                .wrapMessages(new CondensedMessageTracer(System.out));


            Launcher<LanguageClient> clientLauncher = clientLauncherBuilder.create();

            Future<Void> connectionClosed = clientLauncher.startListening();
            glacioLanguageServer.connect(clientLauncher.getRemoteProxy());
            glacioLanguageServer.setMessageProducer(clientLauncherBuilder.getMessageProducer());
            System.out.println("Connected to client");

            connectionClosed.get();
            System.out.println("Disconnected");
        }
    }
}
