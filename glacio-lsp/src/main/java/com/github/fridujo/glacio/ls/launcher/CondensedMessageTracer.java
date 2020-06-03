package com.github.fridujo.glacio.ls.launcher;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.lsp4j.jsonrpc.JsonRpcException;
import org.eclipse.lsp4j.jsonrpc.MessageConsumer;
import org.eclipse.lsp4j.jsonrpc.MessageIssueException;
import org.eclipse.lsp4j.jsonrpc.json.MessageJsonHandler;
import org.eclipse.lsp4j.jsonrpc.json.StreamMessageConsumer;
import org.eclipse.lsp4j.jsonrpc.messages.Message;
import org.eclipse.lsp4j.jsonrpc.messages.NotificationMessage;
import org.eclipse.lsp4j.jsonrpc.messages.RequestMessage;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseMessage;

public class CondensedMessageTracer implements Function<MessageConsumer, MessageConsumer> {
    private final Consumer<String> printWriter;
    private final Map<String, Instant> sentRequests = new HashMap<>();
    private final Map<String, Instant> receivedRequests = new HashMap<>();

    public CondensedMessageTracer(PrintStream printStream) {
        this(new PrintWriter(printStream));
    }

    public CondensedMessageTracer(PrintWriter printWriter) {
        this(s -> {
            printWriter.println(s);
            printWriter.flush();
        });
    }

    public CondensedMessageTracer(Consumer<String> printWriter) {
        this.printWriter = printWriter;
    }

    @Override
    public MessageConsumer apply(MessageConsumer messageConsumer) {
        return new CondensedTracingMessageConsumer(messageConsumer, printWriter, sentRequests, receivedRequests);
    }

    static final class CondensedTracingMessageConsumer implements MessageConsumer {
        private static final MessageJsonHandler MESSAGE_DEBUG_SERIALIZER = new MessageJsonHandler(Collections.emptyMap());
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("KK:mm:ss a").withZone(Clock.systemDefaultZone().getZone());
        private final MessageConsumer delegate;
        private final Consumer<String> printWriter;
        private final Map<String, Instant> sentRequests;
        private final Map<String, Instant> receivedRequests;

        public CondensedTracingMessageConsumer(MessageConsumer delegate,
                                               Consumer<String> printWriter,
                                               Map<String, Instant> sentRequests,
                                               Map<String, Instant> receivedRequests) {
            this.delegate = delegate;
            this.printWriter = printWriter;
            this.sentRequests = sentRequests;
            this.receivedRequests = receivedRequests;
        }

        @Override
        public void consume(Message message) throws MessageIssueException, JsonRpcException {
            Instant now = Instant.now();
            String date = DATE_TIME_FORMATTER.format(now);
            final String logString;
            if (delegate instanceof StreamMessageConsumer) {
                logString = consumeMessageSending(message, now, date);
            } else {
                logString = consumeMessageReceiving(message, now, date);
            }

            if (logString != null) {
                printWriter.accept(logString);
            }

            delegate.consume(message);
        }

        private String consumeMessageSending(Message message, Instant now, String date) {
            if (message instanceof RequestMessage) {
                RequestMessage requestMessage = (RequestMessage) message;
                String id = requestMessage.getId();
                sentRequests.put(id, now);

                String format = "[%s] Sending request '%s'";
                return String.format(format, date, MESSAGE_DEBUG_SERIALIZER.getGson().toJson(message));
            } else if (message instanceof ResponseMessage) {
                ResponseMessage responseMessage = (ResponseMessage) message;
                String id = responseMessage.getId();
                Instant start = receivedRequests.remove(id);

                long latencyMillis = now.toEpochMilli() - start.toEpochMilli();
                String format = "[%s] Sending response '%s'. Processing request took %s ms";
                return String.format(format, date, MESSAGE_DEBUG_SERIALIZER.getGson().toJson(message), latencyMillis);
            } else if (message instanceof NotificationMessage) {
                String format = "[%s] Sending notification '%s'";
                return String.format(format, date, MESSAGE_DEBUG_SERIALIZER.getGson().toJson(message));
            }
            return null;
        }

        private String consumeMessageReceiving(Message message, Instant now, String date) {
            if (message instanceof RequestMessage) {
                RequestMessage requestMessage = (RequestMessage) message;
                String id = requestMessage.getId();
                receivedRequests.put(id, now);

                String format = "[%s] Received request '%s'";
                return String.format(format, date, MESSAGE_DEBUG_SERIALIZER.getGson().toJson(message));
            } else if (message instanceof ResponseMessage) {
                ResponseMessage responseMessage = (ResponseMessage) message;
                String id = responseMessage.getId();
                Instant start = sentRequests.remove(id);
                long latencyMillis = now.toEpochMilli() - start.toEpochMilli();

                String format = "[%s] Received response '%s' in %s ms";
                return String.format(format, date, MESSAGE_DEBUG_SERIALIZER.getGson().toJson(message), latencyMillis);
            } else if (message instanceof NotificationMessage) {
                String format = "[%s] Received notification '%s'";
                return String.format(format, date, MESSAGE_DEBUG_SERIALIZER.getGson().toJson(message));
            }
            return null;
        }
    }
}
