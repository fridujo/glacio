package com.github.fridujo.glacio.ls.launcher;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.MessageConsumer;
import org.eclipse.lsp4j.jsonrpc.MessageProducer;
import org.eclipse.lsp4j.jsonrpc.json.ConcurrentMessageProcessor;
import org.eclipse.lsp4j.jsonrpc.json.StreamMessageProducer;

public class MessageProducerAwareLauncherBuilder<T> extends Launcher.Builder<T> {

    private StreamMessageProducer messageProducer;

    protected ConcurrentMessageProcessor createMessageProcessor(MessageProducer reader,
                                                                MessageConsumer messageConsumer,
                                                                T remoteProxy) {
        if (reader instanceof StreamMessageProducer) {
            this.messageProducer = (StreamMessageProducer) reader;
        }
        return super.createMessageProcessor(reader, messageConsumer, remoteProxy);
    }

    public StreamMessageProducer getMessageProducer() {
        return messageProducer;
    }
}
