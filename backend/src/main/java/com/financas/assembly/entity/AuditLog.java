package com.financas.assembly.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing audit trail for system actions.
 * Tracks all important user actions for compliance and security.
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_log_user", columnList = "user_id"),
    @Index(name = "idx_audit_log_action", columnList = "action"),
    @Index(name = "idx_audit_log_entity", columnList = "entity_type,entity_id"),
    @Index(name = "idx_audit_log_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "Ação é obrigatória")
    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "entity_type", length = 100)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue; // JSON representation

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue; // JSON representation

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Builder for creating audit logs.
     */
    public static class Builder {
        private User user;
        private String action;
        private String entityType;
        private Long entityId;
        private String oldValue;
        private String newValue;
        private String ipAddress;
        private String userAgent;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public Builder entityId(Long entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder oldValue(String oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        public Builder newValue(String newValue) {
            this.newValue = newValue;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public AuditLog build() {
            AuditLog auditLog = new AuditLog();
            auditLog.user = this.user;
            auditLog.action = this.action;
            auditLog.entityType = this.entityType;
            auditLog.entityId = this.entityId;
            auditLog.oldValue = this.oldValue;
            auditLog.newValue = this.newValue;
            auditLog.ipAddress = this.ipAddress;
            auditLog.userAgent = this.userAgent;
            return auditLog;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
