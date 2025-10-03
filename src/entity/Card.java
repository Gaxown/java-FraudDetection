package entity;

import entity.enums.CardStatus;
import entity.enums.CardType;
import java.time.LocalDate;

public sealed class Card permits DebitCard, CreditCard, PrepaidCard {

    private int cardId;
    private String cardNumber;
    private LocalDate expirationDate;
    private CardStatus cardStatus;
    private int customerId;

    public Card(int cardId, String cardNumber, LocalDate expirationDate, CardStatus cardStatus, int customerId) {
        this.cardId = cardId;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cardStatus = cardStatus;
        this.customerId = customerId;
    }

    // Getters
    public int getCardId() {
        return cardId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public int getCustomerId() {
        return customerId;
    }

    // Setters
    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setCardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public CardType getCardType() {
        if (this instanceof DebitCard) {
            return CardType.DEBIT;
        } else if (this instanceof CreditCard) {
            return CardType.CREDIT;
        } else if (this instanceof PrepaidCard) {
            return CardType.PREPAID;
        }
        throw new IllegalStateException("Unknown card type");
    }
}
