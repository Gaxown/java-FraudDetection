package entity;

import entity.enums.OperationType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CardOperation(
    int operationId,
    LocalDateTime operationDate,
    BigDecimal amount,
    OperationType operationType,
    String location,
    int cardId
) {
    // Convenience methods for backward compatibility
    public OperationType getType() {
        return operationType;
    }

    public int getOperationId() {
        return operationId;
    }

    public LocalDateTime getOperationDate() {
        return operationDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getLocation() {
        return location;
    }

    public int getCardId() {
        return cardId;
    }
}
