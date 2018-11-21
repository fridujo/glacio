package com.github.fridujo.glacio.running.runtime;

public class ExecutionResult {
    private final Status status;
    private final String message;
    private final Throwable cause;

    public ExecutionResult(Status status) {
        this(status, null);
    }

    public ExecutionResult(Status status, String message) {
        this(status, message, null);
    }

    public ExecutionResult(Status status, String message, Throwable cause) {
        this.status = status;
        this.message = message;
        this.cause = cause;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return "ExecutionResult{" +
            "status=" + status +
            ", message='" + message + '\'' +
            ", cause=" + cause +
            '}';
    }
}
