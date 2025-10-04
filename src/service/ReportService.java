package service;

import dao.CardDAO;
import dao.CardOperationDAO;
import entity.Card;
import entity.CardOperation;
import entity.enums.CardStatus;
import entity.enums.OperationType;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {
    private final CardDAO cardDAO;
    private final CardOperationDAO operationDAO;

    public ReportService() {
        this.cardDAO = new CardDAO();
        this.operationDAO = new CardOperationDAO();
    }

    public List<Map.Entry<Integer, Long>> getTop5MostUsedCards() throws SQLException {
        List<CardOperation> operations = operationDAO.findAll();

        Map<Integer, Long> operationsPerCard = operations.stream()
            .collect(Collectors.groupingBy(
                CardOperation::getCardId,
                Collectors.counting()
            ));

        return operationsPerCard.entrySet().stream()
            .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toList());
    }

    public Map<OperationType, BigDecimal> getMonthlyStatistics(YearMonth month) throws SQLException {
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59);

        List<CardOperation> operations = operationDAO.findByDateRange(start, end);

        return operations.stream()
            .collect(Collectors.groupingBy(
                CardOperation::getType,
                Collectors.reducing(
                    BigDecimal.ZERO,
                    CardOperation::getAmount,
                    BigDecimal::add
                )
            ));
    }

    public Map<CardStatus, Long> getCardStatusDistribution() throws SQLException {
        List<Card> cards = cardDAO.findAll();

        return cards.stream()
            .collect(Collectors.groupingBy(
                Card::getStatus,
                Collectors.counting()
            ));
    }

    public Map<String, Object> getDailyOperationsSummary(LocalDateTime date) throws SQLException {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);

        List<CardOperation> dailyOperations = operationDAO.findByDateRange(startOfDay, endOfDay);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalOperations", dailyOperations.size());
        summary.put("totalAmount", dailyOperations.stream()
            .map(CardOperation::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.put("operationsByType", dailyOperations.stream()
            .collect(Collectors.groupingBy(
                CardOperation::getType,
                Collectors.counting()
            )));

        return summary;
    }

    public List<Map.Entry<String, Long>> getMostActiveLocations() throws SQLException {
        List<CardOperation> operations = operationDAO.findAll();

        Map<String, Long> operationsByLocation = operations.stream()
            .collect(Collectors.groupingBy(
                CardOperation::getLocation,
                Collectors.counting()
            ));

        return operationsByLocation.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toList());
    }

    public Map<String, BigDecimal> getAverageAmountByCardType() throws SQLException {
        List<Card> cards = cardDAO.findAll();
        List<CardOperation> operations = operationDAO.findAll();

        Map<Integer, String> cardTypes = cards.stream()
            .collect(Collectors.toMap(
                Card::getId,
                card -> card.getClass().getSimpleName()
            ));

        Map<String, List<BigDecimal>> amountsByType = operations.stream()
            .filter(op -> cardTypes.containsKey(op.getCardId()))
            .collect(Collectors.groupingBy(
                op -> cardTypes.get(op.getCardId()),
                Collectors.mapping(
                    CardOperation::getAmount,
                    Collectors.toList()
                )
            ));

        return amountsByType.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    List<BigDecimal> amounts = entry.getValue();
                    BigDecimal sum = amounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                    return sum.divide(BigDecimal.valueOf(amounts.size()), 2, BigDecimal.ROUND_HALF_UP);
                }
            ));
    }

    public Map<String, Object> generateMonthlyReport(YearMonth month) throws SQLException {
        Map<String, Object> report = new HashMap<>();

        report.put("month", month.toString());
        report.put("statisticsByType", getMonthlyStatistics(month));
        report.put("cardStatusDistribution", getCardStatusDistribution());
        report.put("topCards", getTop5MostUsedCards());
        report.put("activeLocations", getMostActiveLocations());
        report.put("averageAmountsByCardType", getAverageAmountByCardType());

        return report;
    }
}
