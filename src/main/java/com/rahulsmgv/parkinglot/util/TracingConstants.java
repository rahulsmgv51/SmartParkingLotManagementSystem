package com.rahulsmgv.parkinglot.util;

/**
 * Constants used for request tracing and MDC propagation.
 */
public final class TracingConstants {

    private TracingConstants() {
    }

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String SESSION_ID_HEADER = "X-Session-Id";
    public static final String USER_ID_HEADER = "X-User-Id";

    public static final String MDC_CORRELATION_ID_KEY = "correlationId";
    public static final String MDC_SESSION_ID_KEY = "sessionId";
    public static final String MDC_USER_ID_KEY = "userId";
    public static final String MDC_THREAD_ID_KEY = "threadId";
    public static final String MDC_THREAD_NAME_KEY = "threadName";
}
