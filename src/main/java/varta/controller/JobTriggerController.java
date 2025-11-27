package varta.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import varta.service.JobService;

import java.util.List;

@RestController
@RequestMapping("api/job/")
@Slf4j
public class JobTriggerController {

    private final JobLauncher jobLauncher;
    private final JobService jobService;

    private final Job creditUserJob;
    private final Job creditStoreJob;
    private final Job creditCardJob;
    private final Job financialTransactionsJob;


    public JobTriggerController(JobLauncher jobLauncher,
                                List<String> jobNames,
                                JobService jobService,
                                @Qualifier("creditUserJob") Job creditUserJob,
                                @Qualifier("creditStoreJob") Job creditStoreJob,
                                @Qualifier("creditCardJob") Job creditCardJob,
                                @Qualifier("financialTransactionJob") Job financialTransactionJob) {
        this.jobLauncher = jobLauncher;
        this.creditUserJob = creditUserJob;
        this.creditStoreJob = creditStoreJob;
        this.creditCardJob = creditCardJob;
        this.financialTransactionsJob = financialTransactionJob;

        this.jobService = jobService;
    }

    @PostMapping("start/credit-user")
    private void launchCreditUserJob() {
        log.info("Accepted launch credit user job");
        jobService.launchJob(creditUserJob, jobLauncher);
    }

    @PostMapping("start/credit-store")
    private void launchCreditStoreJob() {
        log.info("Accepted launch store user job");
        jobService.launchJob(creditStoreJob, jobLauncher);
    }

    @PostMapping("start/credit-card")
    private void launchCreditCardJob() {
        log.info("Accepted launch credit card job");
        jobService.launchJob(creditCardJob, jobLauncher);
    }

    @PostMapping("start/financial-transaction")
    private void launchFinancialTransactionsJob() {
        log.info("Accepted launch financial transaction job");
        jobService.launchJob(financialTransactionsJob, jobLauncher);
    }
}
