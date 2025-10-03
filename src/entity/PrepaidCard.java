package entity;

import entity.enums.CardStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class PrepaidCard extends Card {
    private BigDecimal availableBalance;

    public PrepaidCard(int cardId, String cardNumber, LocalDate expirationDate, CardStatus cardStatus, int customerId, BigDecimal availableBalance) {
        super(cardId, cardNumber, expirationDate, cardStatus, customerId);
        this.availableBalance = availableBalance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }
}
