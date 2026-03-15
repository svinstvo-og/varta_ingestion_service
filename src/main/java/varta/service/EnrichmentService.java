package varta.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import varta.dto.EnrichedTransactionDto;
import varta.dto.TimePeriod;
import varta.model.pgsql.CreditTransaction;
import varta.repository.pgsql.CreditTransactionRepository;
import varta.util.Converter;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EnrichmentService {

    private final CreditTransactionRepository creditTransactionRepository;

    public EnrichmentService(CreditTransactionRepository creditTransactionRepository) {
        this.creditTransactionRepository = creditTransactionRepository;
    }

    public EnrichedTransactionDto enrichCreditTransaction(CreditTransaction transaction) {
        EnrichedTransactionDto enrichedTransaction = new EnrichedTransactionDto();

        Map<TimePeriod, List<CreditTransaction>> latestTransactions = getLatestTransactions(transaction);

        List<CreditTransaction> transactionsLast30D = latestTransactions.get(TimePeriod.LAST_30DAYS);
        List<CreditTransaction> transactionsLast24H = latestTransactions.get(TimePeriod.LAST_DAY);
        List<CreditTransaction> transactionsLast1H = latestTransactions.get(TimePeriod.LAST_HOUR);

        enrichedTransaction.setVelocity1H(transactionsLast1H.size());
        enrichedTransaction.setVelocity24H(transactionsLast24H.size());

        enrichedTransaction.setDistinctMerchants1H(countUniqueMerchants(transactionsLast1H));

        setMonetaryFeatures(enrichedTransaction, transactionsLast30D, transaction.getTransactionAmount().doubleValue());

        return enrichedTransaction;
    }

    private int countUniqueMerchants(List<CreditTransaction> transactions) {
        int counter = 0;
        Set<Long> seenIds = new HashSet<>();

        for (CreditTransaction transaction : transactions) {
            if (!seenIds.contains(transaction.getTransactionInternalId())) {
                seenIds.add(transaction.getTransactionInternalId());
                counter++;
            }
        }
        return counter;
    }

    private Map<TimePeriod, List<CreditTransaction>> getLatestTransactions(CreditTransaction transaction) {
        HashMap<TimePeriod, List<CreditTransaction>> latestTransactions = new HashMap<>();

        List<CreditTransaction> transactionsLast30D = creditTransactionRepository.
                getCreditTransactionByInterval(Converter.daysToMinutes(30));

        List<CreditTransaction> transactionsLast1H = new ArrayList<>();
        List<CreditTransaction> transactionsLast24H = new ArrayList<>();

        LocalDateTime transactionCreatedAt = transaction.getProcessedAt();

        for (CreditTransaction ct : transactionsLast30D) {
            if (ct.getProcessedAt().isAfter(transactionCreatedAt.minusMinutes(60))) {
                transactionsLast1H.add(ct);
                transactionsLast24H.add(ct);
            }
            else if (ct.getProcessedAt().isAfter(transactionCreatedAt.minusDays(1))) {
                transactionsLast24H.add(ct);
            }
        }
        latestTransactions.put(TimePeriod.LAST_HOUR, transactionsLast1H);
        latestTransactions.put(TimePeriod.LAST_DAY, transactionsLast24H);
        latestTransactions.put(TimePeriod.LAST_30DAYS, transactionsLast30D);

        return latestTransactions;
    }

    private void setMonetaryFeatures(EnrichedTransactionDto enrichedTransaction, List<CreditTransaction> transactions, double enrichedTransactionAmount) {
        if (transactions.isEmpty()) {
            enrichedTransaction.setZScore(0.0);
            enrichedTransaction.setRatioToMedian(0.0);
            enrichedTransaction.setMaxSingleJump(0.0);
            enrichedTransaction.setAvgSpend30D(0.0);
            return;
        }

        double totalSpent = 0;
        double max = 0;
        double tempAmount;
        double median;
        double mean;
        ArrayList<Double> amounts = new ArrayList<>();

        for (CreditTransaction transaction : transactions) {
            // mean
            tempAmount = transaction.getTransactionAmount().doubleValue();
            totalSpent += tempAmount;

            // median
            amounts.add(tempAmount);

            // max val
            if (tempAmount > max) max = tempAmount;
        }

        // median calculations
        amounts.sort(null);
        if (amounts.size() % 2 == 0) {
            median = (amounts.get(amounts.size() / 2 - 1) + amounts.get(amounts.size() / 2)) / 2;
        } else {
            median = amounts.get(amounts.size() / 2);
        }

        mean = totalSpent / amounts.size();

        double standardDeviation = calculateStandardDeviation(amounts, mean);
        if (standardDeviation == 0) {
            enrichedTransaction.setZScore(0.0);
        } else {
            enrichedTransaction.setZScore((enrichedTransactionAmount - mean) / standardDeviation);
        }

        if (median == 0) {
            enrichedTransaction.setRatioToMedian(0.0);
        } else {
            enrichedTransaction.setRatioToMedian(enrichedTransactionAmount / median);
        }

        if (max == 0) {
             enrichedTransaction.setMaxSingleJump(0.0);
        } else {
            enrichedTransaction.setMaxSingleJump(enrichedTransactionAmount / max);
        }

        enrichedTransaction.setAvgSpend30D(mean);
    }

    private double calculateStandardDeviation(List<Double> amounts, double mean) {
        if (amounts.size() <= 1) {
            return 0.0;
        }

        double varianceSum = 0.0;
        double standardDeviation;

        for (Double amount : amounts) {
            double diff = amount - mean;
            varianceSum += diff * diff;
        }

        standardDeviation = Math.sqrt(varianceSum / (amounts.size() - 1));

        return standardDeviation;
    }
}