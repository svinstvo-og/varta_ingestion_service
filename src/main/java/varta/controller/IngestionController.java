package varta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import varta.model.pgsql.Sixm;
import varta.service.NormalizationService;
import varta.service.TransactionRaw6mService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("api/ingestion/")
@RestController
@Slf4j
public class IngestionController {

    final
    NormalizationService normalizationService;

    final
    TransactionRaw6mService transactionRaw6mService;

    public IngestionController(TransactionRaw6mService transactionRaw6mService, NormalizationService normalizationService) {
        this.transactionRaw6mService = transactionRaw6mService;
        this.normalizationService = normalizationService;
    }

    @GetMapping({"/test/datasets/6m/{id}"})
    public Sixm fetchTransaction(@PathVariable Integer id) {
        log.info("Accepted 'Get transaction by {} id' request", id);
        Optional<Sixm> transaction = transactionRaw6mService.getTransactionById(id);
        if (transaction.isPresent()) {
            log.info("Transaction found: {}", transaction.get().getId());
            return transaction.get();
        }
        else {
            log.warn("Transaction not found: {}", id);
            return null;
        }
    }

    @GetMapping("/test/financial_transaction/{id}")
    public void fetchRawTransaction(@PathVariable Long id) {
        log.info("Accepted 'Get raw transaction by {} id' request", id);
        normalizationService.testRawTransactionRead(id);
    }
}
