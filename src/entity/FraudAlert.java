package entity;

import entity.enums.AlertLevel;
import java.time.LocalDateTime;

public record FraudAlert(
    int alertId,
    String description,
    AlertLevel alertLevel,
    int cardId,
    LocalDateTime creationDate
) {}
