package service;

import dao.CardDAO;
import entity.*;
import entity.enums.CardStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class CardService {
    private final CardDAO cardDAO;
    private final Random random;

    public CardService() {
        this.cardDAO = new CardDAO();
        this.random = new Random();
    }

    private String generateCardNumber() {
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            number.append(random.nextInt(10));
        }
        return number.toString();
    }

    public DebitCard createDebitCard(int customerId, BigDecimal dailyLimit) throws SQLException {
        String number = generateCardNumber();
        LocalDate expiration = LocalDate.now().plusYears(3);

        DebitCard card = new DebitCard(0, number, expiration, CardStatus.ACTIVE, customerId, dailyLimit);
        return (DebitCard) cardDAO.save(card);
    }

    public CreditCard createCreditCard(int customerId, BigDecimal monthlyLimit, BigDecimal interestRate) throws SQLException {
        String number = generateCardNumber();
        LocalDate expiration = LocalDate.now().plusYears(3);

        CreditCard card = new CreditCard(0, number, expiration, CardStatus.ACTIVE, customerId, monthlyLimit, interestRate);
        return (CreditCard) cardDAO.save(card);
    }

    public PrepaidCard createPrepaidCard(int customerId, BigDecimal initialBalance) throws SQLException {
        String number = generateCardNumber();
        LocalDate expiration = LocalDate.now().plusYears(3);

        PrepaidCard card = new PrepaidCard(0, number, expiration, CardStatus.ACTIVE, customerId, initialBalance);
        return (PrepaidCard) cardDAO.save(card);
    }

    public boolean activateCard(int cardId) throws SQLException {
        Optional<Card> cardOpt = cardDAO.findById(cardId);
        if (cardOpt.isEmpty()) {
            throw new IllegalArgumentException("Card not found");
        }

        Card card = cardOpt.get();
        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new IllegalStateException("Card is already active");
        }

        card.setStatus(CardStatus.ACTIVE);
        return cardDAO.update(card);
    }

    public boolean suspendCard(int cardId) throws SQLException {
        Optional<Card> cardOpt = cardDAO.findById(cardId);
        if (cardOpt.isEmpty()) {
            throw new IllegalArgumentException("Card not found");
        }

        Card card = cardOpt.get();
        card.setStatus(CardStatus.SUSPENDED);
        return cardDAO.update(card);
    }

    public boolean blockCard(int cardId) throws SQLException {
        Optional<Card> cardOpt = cardDAO.findById(cardId);
        if (cardOpt.isEmpty()) {
            throw new IllegalArgumentException("Card not found");
        }

        Card card = cardOpt.get();
        card.setStatus(CardStatus.BLOCKED);
        return cardDAO.update(card);
    }

    public boolean verifyLimit(int cardId, BigDecimal amount) throws SQLException {
        Optional<Card> cardOpt = cardDAO.findById(cardId);
        if (cardOpt.isEmpty()) {
            throw new IllegalArgumentException("Card not found");
        }

        Card card = cardOpt.get();

        if (card instanceof DebitCard) {
            DebitCard debitCard = (DebitCard) card;
            return amount.compareTo(debitCard.getDailyLimit()) <= 0;
        } else if (card instanceof CreditCard) {
            CreditCard creditCard = (CreditCard) card;
            return amount.compareTo(creditCard.getMonthlyLimit()) <= 0;
        } else if (card instanceof PrepaidCard) {
            PrepaidCard prepaidCard = (PrepaidCard) card;
            return amount.compareTo(prepaidCard.getAvailableBalance()) <= 0;
        } else {
            return false;
        }
    }

    public Optional<Card> findCardById(int cardId) throws SQLException {
        return cardDAO.findById(cardId);
    }

    public List<Card> findCardsByCustomer(int customerId) throws SQLException {
        return cardDAO.findByCustomerId(customerId);
    }

    public List<Card> findAllCards() throws SQLException {
        return cardDAO.findAll();
    }

    public boolean updateCard(Card card) throws SQLException {
        return cardDAO.update(card);
    }

    public boolean deleteCard(int cardId) throws SQLException {
        return cardDAO.delete(cardId);
    }
}
