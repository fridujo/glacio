package com.github.fridujo.glacio.ls.launcher;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import com.github.fridujo.glacio.ls.GlacioLanguageServer;
import com.github.fridujo.glacio.ls.tools.FileLogger;

public class IoLauncher {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FileLogger logger = new FileLogger("glacio-lsp-messages.log");
        GlacioLanguageServer glacioLanguageServer = new GlacioLanguageServer();
        Launcher<LanguageClient> serverLauncher = new LSPLauncher.Builder<LanguageClient>()
            .setLocalService(glacioLanguageServer)
            .setRemoteInterface(LanguageClient.class)
            .setInput(System.in)
            .setOutput(System.out)
            .wrapMessages(new CondensedMessageTracer(logger::log))
            .create();
        Future<Void> serverTermination = serverLauncher.startListening();
        glacioLanguageServer.connect(serverLauncher.getRemoteProxy());
        serverTermination.get();
    }
}
