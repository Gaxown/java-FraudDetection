package entity;

import entity.enums.CardStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class DebitCard extends Card {
    private BigDecimal dailyLimit;

    public DebitCard(int cardId, String cardNumber, LocalDate expirationDate, CardStatus cardStatus, int customerId, BigDecimal dailyLimit) {
        super(cardId, cardNumber, expirationDate, cardStatus, customerId);
        this.dailyLimit = dailyLimit;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }
}
