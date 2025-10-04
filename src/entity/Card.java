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

    // Additional getters for compatibility
    public CardStatus getStatus() {
        return cardStatus;
    }

    public void setStatus(CardStatus status) {
        this.cardStatus = status;
    }

    public CardType getCardType() {
        return switch (this) {
            case DebitCard ignored -> CardType.DEBIT;
            case CreditCard ignored -> CardType.CREDIT;
            case PrepaidCard ignored -> CardType.PREPAID;
        };
    }
}
