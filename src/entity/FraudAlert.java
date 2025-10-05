package entity;

import entity.enums.AlertLevel;
import java.time.LocalDateTime;

public record FraudAlert(
    int alertId,
    String description,
    AlertLevel alertLevel,
    int cardId,
    LocalDateTime creationDate
) {
    public int getAlertId() {
        return alertId;
    }

    public String getDescription() {
        return description;
    }

    public AlertLevel getAlertLevel() {
        return alertLevel;
    }

    public int getCardId() {
        return cardId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
