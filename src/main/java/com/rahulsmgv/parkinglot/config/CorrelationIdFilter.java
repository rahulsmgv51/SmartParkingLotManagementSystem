package com.rahulsmgv.parkinglot.config;

import com.rahulsmgv.parkinglot.util.TracingConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends HttpFilter {

        /**
         * Intercepts each HTTP request to establish tracing context.
         *
         * The filter reads incoming trace headers when present, generates
         * a correlation id when missing, and stores tracing attributes in MDC.
         */
        @Override
        protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                        throws IOException, ServletException {

                String correlationId = request.getHeader(TracingConstants.CORRELATION_ID_HEADER);

                if (correlationId == null || correlationId.isBlank()) {
                        correlationId = UUID.randomUUID().toString();
                }

                String userId = request.getHeader(TracingConstants.USER_ID_HEADER);
                if (userId == null || userId.isBlank()) {
                        userId = "anonymous";
                }

                String sessionId = request.getHeader(TracingConstants.SESSION_ID_HEADER);
                if (sessionId == null || sessionId.isBlank()) {
                        // Create or reuse an HTTP session id for requests that do not provide one.
                        HttpSession session = request.getSession(true);
                        sessionId = session.getId();
                }

                String threadId = String.valueOf(Thread.currentThread().getId());
                String threadName = Thread.currentThread().getName();

                MDC.put(TracingConstants.MDC_CORRELATION_ID_KEY, correlationId);
                MDC.put(TracingConstants.MDC_USER_ID_KEY, userId);
                MDC.put(TracingConstants.MDC_SESSION_ID_KEY, sessionId);
                MDC.put(TracingConstants.MDC_THREAD_ID_KEY, threadId);
                MDC.put(TracingConstants.MDC_THREAD_NAME_KEY, threadName);

                response.setHeader(TracingConstants.CORRELATION_ID_HEADER, correlationId);
                response.setHeader(TracingConstants.SESSION_ID_HEADER, sessionId);

                log.info("Request received: method={}, uri={}, correlationId={}, sessionId={}, userId={}, threadName={}, threadId={}",
                                request.getMethod(),
                                request.getRequestURI(),
                                correlationId,
                                sessionId,
                                userId,
                                threadName,
                                threadId);

                try {
                        chain.doFilter(request, response);
                } finally {
                        log.info("Request completed: method={}, uri={}, status={}, correlationId={}, sessionId={}, userId={}, threadName={}, threadId={}",
                                        request.getMethod(),
                                        request.getRequestURI(),
                                        response.getStatus(),
                                        correlationId,
                                        sessionId,
                                        userId,
                                        threadName,
                                        threadId);
                        // Clear MDC entries to prevent tracing data from leaking into subsequent
                        // requests.
                        MDC.remove(TracingConstants.MDC_CORRELATION_ID_KEY);
                        MDC.remove(TracingConstants.MDC_USER_ID_KEY);
                        MDC.remove(TracingConstants.MDC_SESSION_ID_KEY);
                        MDC.remove(TracingConstants.MDC_THREAD_ID_KEY);
                        MDC.remove(TracingConstants.MDC_THREAD_NAME_KEY);
                }
        }
}
