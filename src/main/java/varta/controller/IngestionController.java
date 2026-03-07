package varta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import varta.service.NormalizationService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("api/ingestion/")
@RestController
@Slf4j
public class IngestionController {

    private final NormalizationService normalizationService;

    public IngestionController(NormalizationService normalizationService) {
        this.normalizationService = normalizationService;
    }

    @PostMapping("launch")
    private void normalizeAllTables() {
        normalizationService.normalizeAllTables();
    }
}
