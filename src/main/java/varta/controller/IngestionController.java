package varta.controller;

import varta.service.NormalizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/ingestion/")
@RestController
@Slf4j
public class IngestionController {

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
}
