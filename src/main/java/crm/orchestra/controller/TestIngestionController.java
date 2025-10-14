package crm.orchestra.controller;

import crm.orchestra.model.Sixm;
import crm.orchestra.service.TransactionRaw6mService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("api/test/ingestion/")
@RestController
@Slf4j
public class TestIngestionController {

    final
    TransactionRaw6mService transactionRaw6mService;

    public TestIngestionController(TransactionRaw6mService transactionRaw6mService) {
        this.transactionRaw6mService = transactionRaw6mService;
    }

    @GetMapping({"datasets/6m/{id}"})
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
}
