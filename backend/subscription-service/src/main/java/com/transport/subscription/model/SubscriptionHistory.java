package com.transport.subscription.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Entité représentant l'historique des changements d'un abonnement
 */
@Entity
@Table(name = "subscription_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", updatable = false, nullable = false)
    private UUID historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false, foreignKey = @ForeignKey(name = "fk_history_subscription"))
    @ToString.Exclude
    private Subscription subscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20)
    private SubscriptionStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private SubscriptionStatus newStatus;

    @Column(name = "event_type", length = 64, nullable = false)
    private String eventType;

    @Column(name = "event_date", nullable = false)
    private OffsetDateTime eventDate;

    @Column(name = "performed_by")
    private UUID performedBy;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @PrePersist
    protected void onCreate() {
        if (eventDate == null) {
            eventDate = OffsetDateTime.now();
        }
    }
}

