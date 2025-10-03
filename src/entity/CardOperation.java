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
) {}
