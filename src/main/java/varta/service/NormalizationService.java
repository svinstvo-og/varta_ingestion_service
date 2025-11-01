package varta.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import varta.model.mysql.RawFinancialTransaction;
import varta.repository.mysql.RawFinancialTransactionRepository;

import java.util.Optional;

@Service
@Slf4j
public class NormalizationService {

    final
    RawFinancialTransactionRepository rawFinancialTransactionRepository;

    public NormalizationService(RawFinancialTransactionRepository rawFinancialTransactionRepository) {
        this.rawFinancialTransactionRepository = rawFinancialTransactionRepository;
    }

    public RawFinancialTransaction testRawTransactionRead(Long id) {
        log.info("testRawTransactionRead");
        Optional<RawFinancialTransaction> financialTransaction = rawFinancialTransactionRepository.findById(Long.valueOf(id));
        if (financialTransaction.isPresent()) {
            log.info("Found transaction: " + financialTransaction.get().getTransactionUniqueId());
            return financialTransaction.get();
        }
        log.info("No transaction found");
        return null;
    }
}
