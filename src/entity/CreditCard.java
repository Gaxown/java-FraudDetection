package entity;

import entity.enums.CardStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class CreditCard extends Card {
    private BigDecimal monthlyLimit;
    private BigDecimal interestRate;

    public CreditCard(int cardId, String cardNumber, LocalDate expirationDate, CardStatus cardStatus, int customerId, BigDecimal monthlyLimit, BigDecimal interestRate) {
        super(cardId, cardNumber, expirationDate, cardStatus, customerId);
        this.monthlyLimit = monthlyLimit;
        this.interestRate = interestRate;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
