package varta.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import varta.model.mysql.RawFinancialTransaction;
import varta.repository.mysql.RawFinancialTransactionRepository;
import varta.service.messaging.FatTransactionPublisher;
import varta.dto.FatTransactionDto;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class NormalizationService {

    private final RawFinancialTransactionRepository rawFinancialTransactionRepository;

    private final JobLauncher jobLauncher;
    private final JobService jobService;

    private final Job creditUserJob;
    private final Job creditStoreJob;
    private final Job creditCardJob;
    private final Job financialTransactionJob;
    private final Job creditTransacitonJob;

    private final FatTransactionPublisher fatTransactionPublisher;

    public NormalizationService(RawFinancialTransactionRepository rawFinancialTransactionRepository,
                                JobLauncher jobLauncher,
                                JobService jobService,
                                @Qualifier("creditUserJob") Job creditUserJob,
                                @Qualifier("creditStoreJob") Job creditStoreJob,
                                @Qualifier("creditCardJob") Job creditCardJob,
                                @Qualifier("financialTransactionJob") Job financialTransactionJob,
                                @Qualifier("creditTransactionJob") Job creditTransacitonJob,
                                FatTransactionPublisher fatTransactionPublisher) {
        this.rawFinancialTransactionRepository = rawFinancialTransactionRepository;
        this.jobLauncher = jobLauncher;
        this.creditUserJob = creditUserJob;
        this.creditStoreJob = creditStoreJob;
        this.creditCardJob = creditCardJob;
        this.financialTransactionJob = financialTransactionJob;
        this.creditTransacitonJob = creditTransacitonJob;
        this.fatTransactionPublisher = fatTransactionPublisher;

        this.jobService = jobService;
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

    public void normalizeAllTables() {
        log.info("LAUNCHING CREDIT USER JOB");
        launchCreditUserJob();
        log.info("LAUNCHING CREDIT STORE JOB");
        launchCreditStoreJob();
        log.info("LAUNCHING CREDIT CARD JOB");
        launchCreditCardJob();
        log.info("LAUNCHING FINANCIAL TRANSACTION JOB");
        launchFinancialTransactionJob();
        log.info("LAUNCHING CREDIT TRANSACTION JOB");
        launchCreditTransactionJob();
        log.info("NORMALIZATION IS FINISHED");
    }

    // Job triggers

    public void launchCreditUserJob() {
        jobService.launchJob(creditUserJob, jobLauncher);
    }

    public void launchCreditStoreJob() {
        jobService.launchJob(creditStoreJob, jobLauncher);
    }

    public void launchCreditCardJob() {
        jobService.launchJob(creditCardJob, jobLauncher);
    }

    public void launchFinancialTransactionJob() {
        jobService.launchJob(financialTransactionJob, jobLauncher);
    }

    public void launchCreditTransactionJob() {
        jobService.launchJob(creditTransacitonJob, jobLauncher);
    }

    public void publishFatTransaction(FatTransactionDto payload) {
        fatTransactionPublisher.publish(payload);
    }
}
