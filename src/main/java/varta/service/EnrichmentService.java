package varta.service;

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

//    private RedisTemplate<>
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

        return enrichedTransaction;
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
}
