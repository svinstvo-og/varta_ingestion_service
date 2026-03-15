package varta.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import varta.dto.FatTransactionDto;
import varta.model.mysql.RawFinancialTransaction;
import varta.repository.mysql.RawFinancialTransactionRepository;
import varta.service.messaging.FatTransactionPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NormalizationServiceTest {

    @Mock
    private RawFinancialTransactionRepository rawFinancialTransactionRepository;

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private JobService jobService;

    @Mock
    private Job creditUserJob;

    @Mock
    private Job creditStoreJob;

    @Mock
    private Job creditCardJob;

    @Mock
    private Job financialTransactionJob;

    @Mock
    private Job creditTransactionJob;

    @Mock
    private FatTransactionPublisher fatTransactionPublisher;

    private NormalizationService normalizationService;

    @BeforeEach
    void setUp() {
        normalizationService = new NormalizationService(
                rawFinancialTransactionRepository,
                jobLauncher,
                jobService,
                creditUserJob,
                creditStoreJob,
                creditCardJob,
                financialTransactionJob,
                creditTransactionJob,
                fatTransactionPublisher
        );
    }

    @Test
    @DisplayName("should find raw transaction by id")
    void testRawTransactionRead_findsTransaction() {
        RawFinancialTransaction transaction = new RawFinancialTransaction();
        transaction.setTransactionUniqueId("unique-id");

        when(rawFinancialTransactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        RawFinancialTransaction result = normalizationService.testRawTransactionRead(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTransactionUniqueId()).isEqualTo("unique-id");
    }

    @Test
    @DisplayName("should return null when raw transaction not found")
    void testRawTransactionRead_returnsNullWhenNotFound() {
        when(rawFinancialTransactionRepository.findById(1L)).thenReturn(Optional.empty());

        RawFinancialTransaction result = normalizationService.testRawTransactionRead(1L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("should normalize all tables by launching all jobs")
    void normalizeAllTables_launchesAllJobs() {
        normalizationService.normalizeAllTables();

        verify(jobService).launchJob(creditUserJob, jobLauncher);
        verify(jobService).launchJob(creditStoreJob, jobLauncher);
        verify(jobService).launchJob(creditCardJob, jobLauncher);
        verify(jobService).launchJob(financialTransactionJob, jobLauncher);
        verify(jobService).launchJob(creditTransactionJob, jobLauncher);
    }

    @Test
    @DisplayName("should launch credit user job")
    void launchCreditUserJob_launchesJob() {
        normalizationService.launchCreditUserJob();
        verify(jobService).launchJob(creditUserJob, jobLauncher);
    }

    @Test
    @DisplayName("should launch credit store job")
    void launchCreditStoreJob_launchesJob() {
        normalizationService.launchCreditStoreJob();
        verify(jobService).launchJob(creditStoreJob, jobLauncher);
    }

    @Test
    @DisplayName("should launch credit card job")
    void launchCreditCardJob_launchesJob() {
        normalizationService.launchCreditCardJob();
        verify(jobService).launchJob(creditCardJob, jobLauncher);
    }

    @Test
    @DisplayName("should launch financial transaction job")
    void launchFinancialTransactionJob_launchesJob() {
        normalizationService.launchFinancialTransactionJob();
        verify(jobService).launchJob(financialTransactionJob, jobLauncher);
    }

    @Test
    @DisplayName("should launch credit transaction job")
    void launchCreditTransactionJob_launchesJob() {
        normalizationService.launchCreditTransactionJob();
        verify(jobService).launchJob(creditTransactionJob, jobLauncher);
    }

    @Test
    @DisplayName("should publish fat transaction")
    void publishFatTransaction_publishesPayload() {
        FatTransactionDto payload = new FatTransactionDto();
        normalizationService.publishFatTransaction(payload);

        verify(fatTransactionPublisher).publish(payload);
    }
}

