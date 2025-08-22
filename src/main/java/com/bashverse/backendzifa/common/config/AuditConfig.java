package com.bashverse.backendzifa.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
/**
 * Configuration class for logging and audit-related settings.
 */
@Configuration
public class AuditConfig {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");

    @PostConstruct
    public void init() {
        auditLogger.info("Audit logging configuration initialized.");
    }

    /**
     * Example method to log audit events.
     *
     * @param event description of the audit event
     * @param userId user ID associated with the event
     */
    public void logAuditEvent(String event, String userId) {
        // You can implement more advanced structured logging with MDC or JSON here
        auditLogger.info("AUDIT EVENT: UserId={}, Event={}", userId, event);
    }
}
