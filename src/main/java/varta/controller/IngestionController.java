package varta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import varta.service.NormalizationService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("api/ingestion/")
@RestController
@Slf4j
public class IngestionController {

    @Autowired
    private

    final
    NormalizationService normalizationService;

    public IngestionController(NormalizationService normalizationService) {
        this.normalizationService = normalizationService;
    }

    @GetMapping("/test/financial_transaction/{id}")
    public void fetchRawTransaction(@PathVariable Long id) {
        log.info("Accepted 'Get raw transaction by {} id' request", id);
        normalizationService.testRawTransactionRead(id);
    }

    @PostMapping("test/credit-user")
    public void createCreditUser() {
        log.info("Accepted 'Create credit user' request");

    }
}
