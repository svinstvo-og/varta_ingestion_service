package varta.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import varta.service.JobService;
import varta.service.NormalizationService;

import java.util.List;

@RestController
@RequestMapping("api/job/")
@Slf4j
public class JobTriggerController {

    private final NormalizationService normalizationService;

    public JobTriggerController(NormalizationService normalizationService) {
        this.normalizationService = normalizationService;
    }

    @PostMapping("start/credit-user")
    private void launchCreditUserJob() {
        log.info("Accepted launch credit user job");
        normalizationService.launchCreditUserJob();
    }

    @PostMapping("start/credit-store")
    private void launchCreditStoreJob() {
        log.info("Accepted launch store user job");
        normalizationService.launchCreditStoreJob();
    }

    @PostMapping("start/credit-card")
    private void launchCreditCardJob() {
        log.info("Accepted launch credit card job");
        normalizationService.launchCreditCardJob();
    }

    @PostMapping("start/financial-transaction")
    private void launchFinancialTransactionJob() {
        log.info("Accepted launch financial transaction job");
        normalizationService.launchFinancialTransactionJob();
    }

    @PostMapping("start/credit-transaction")
    private void launchCreditTransactionJob() {
        log.info("Launching credit transaction job");
        normalizationService.launchCreditTransactionJob();
    }
}
