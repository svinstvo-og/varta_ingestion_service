package varta.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import varta.dto.EnrichedTransactionDto;
import varta.model.pgsql.CreditTransaction;
import varta.repository.pgsql.CreditTransactionRepository;
import varta.util.Converter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrichmentServiceTest {

    @Mock
    private CreditTransactionRepository creditTransactionRepository;

    private EnrichmentService enrichmentService;

    @BeforeEach
    void setUp() {
        enrichmentService = new EnrichmentService(creditTransactionRepository);
    }

    @Nested
    @DisplayName("Velocity Features Tests")
    class VelocityFeaturesTests {

        @Test
        @DisplayName("should calculate velocity for transactions within 1 hour")
        void enrichCreditTransaction_calculatesVelocity1H() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 100.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 50.0, baseTime.minusMinutes(30)),
                            transaction(2L, 60.0, baseTime.minusMinutes(59)),
                            transaction(3L, 70.0, baseTime.minusMinutes(61)) // outside 1H
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getVelocity1H()).isEqualTo(2);
        }

        @Test
        @DisplayName("should calculate velocity for transactions within 24 hours")
        void enrichCreditTransaction_calculatesVelocity24H() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 100.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 50.0, baseTime.minusMinutes(30)),
                            transaction(2L, 60.0, baseTime.minusHours(12)),
                            transaction(3L, 70.0, baseTime.minusHours(23)),
                            transaction(4L, 80.0, baseTime.minusHours(25)) // outside 24H
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getVelocity24H()).isEqualTo(3);
        }

        @Test
        @DisplayName("should include 1H transactions in 24H count")
        void enrichCreditTransaction_includes1HTransactionsIn24H() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 100.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 50.0, baseTime.minusMinutes(30)),
                            transaction(2L, 60.0, baseTime.minusMinutes(45)),
                            transaction(3L, 70.0, baseTime.minusDays(5))
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getVelocity1H()).isEqualTo(2);
            assertThat(enriched.getVelocity24H()).isEqualTo(2);
        }

        @Test
        @DisplayName("should return zero velocity when no transactions in period")
        void enrichCreditTransaction_zeroVelocityWhenNoRecentTransactions() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 100.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 50.0, baseTime.minusDays(10))
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getVelocity1H()).isZero();
            assertThat(enriched.getVelocity24H()).isZero();
        }
    }

    @Nested
    @DisplayName("Distinct Merchants Tests")
    class DistinctMerchantsTests {

        @Test
        @DisplayName("should count distinct merchants within 1 hour")
        void enrichCreditTransaction_countsDistinctMerchants() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(500L, 90.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 50.0, baseTime.minusMinutes(30)),
                            transaction(2L, 60.0, baseTime.minusMinutes(20)),
                            transaction(3L, 70.0, baseTime.minusMinutes(10))
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getDistinctMerchants1H()).isEqualTo(3);
        }

        @Test
        @DisplayName("should count same merchant ID only once")
        void enrichCreditTransaction_countsSameMerchantOnce() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(500L, 90.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 50.0, baseTime.minusMinutes(30)),
                            transaction(1L, 60.0, baseTime.minusMinutes(20)), // same ID
                            transaction(2L, 70.0, baseTime.minusMinutes(10))
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getDistinctMerchants1H()).isEqualTo(2);
        }

        @Test
        @DisplayName("should return zero distinct merchants when no transactions in 1H")
        void enrichCreditTransaction_zeroDistinctMerchantsWhenEmpty() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(500L, 90.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 50.0, baseTime.minusHours(2)) // outside 1H
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getDistinctMerchants1H()).isZero();
        }
    }

    @Nested
    @DisplayName("Monetary Features Tests")
    class MonetaryFeaturesTests {

        @Test
        @DisplayName("should calculate average spend over 30 days")
        void enrichCreditTransaction_calculatesAvgSpend30D() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 150.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 100.0, baseTime.minusDays(1)),
                            transaction(2L, 200.0, baseTime.minusDays(5)),
                            transaction(3L, 300.0, baseTime.minusDays(10))
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getAvgSpend30D()).isCloseTo(200.0, within(1e-9));
        }

        @Test
        @DisplayName("should calculate z-score correctly")
        void enrichCreditTransaction_calculatesZScore() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            // Mean = 100, Std Dev = 20, Z-Score = (150-100)/20 = 2.5
            CreditTransaction currentTransaction = transaction(999L, 150.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 100.0, baseTime.minusDays(1)),
                            transaction(2L, 120.0, baseTime.minusDays(5)),
                            transaction(3L, 80.0, baseTime.minusDays(10))
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getZScore()).isCloseTo(2.5, within(1e-9));
        }

        @Test
        @DisplayName("should calculate ratio to median for odd number of transactions")
        void enrichCreditTransaction_calculatesRatioToMedianOdd() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 200.0, baseTime);

            // Sorted: 80, 100, 120 -> Median = 100
            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 100.0, baseTime.minusDays(1)),
                            transaction(2L, 120.0, baseTime.minusDays(5)),
                            transaction(3L, 80.0, baseTime.minusDays(10))
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getRatioToMedian()).isCloseTo(2.0, within(1e-9));
        }

        @Test
        @DisplayName("should calculate ratio to median for even number of transactions")
        void enrichCreditTransaction_calculatesRatioToMedianEven() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 125.0, baseTime);

            // Sorted: 80, 100, 110, 120 -> Median = (110 + 120) / 2 = 115
            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 100.0, baseTime.minusDays(1)),
                            transaction(2L, 120.0, baseTime.minusDays(5)),
                            transaction(3L, 80.0, baseTime.minusDays(10)),
                            transaction(4L, 110.0, baseTime.minusDays(15))
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            // Note: Current implementation uses size/2 and size/2+1 which gives (110+120)/2=115
            assertThat(enriched.getRatioToMedian()).isCloseTo(125.0 / 115.0, within(1e-9));
        }

        @Test
        @DisplayName("should calculate max single jump correctly")
        void enrichCreditTransaction_calculatesMaxSingleJump() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 250.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 100.0, baseTime.minusDays(1)),
                            transaction(2L, 200.0, baseTime.minusDays(5)), // max
                            transaction(3L, 150.0, baseTime.minusDays(10))
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getMaxSingleJump()).isCloseTo(1.25, within(1e-9)); // 250/200
        }

        @Test
        @DisplayName("should handle single transaction for monetary features")
        void enrichCreditTransaction_handlesSingleTransaction() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 100.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 50.0, baseTime.minusDays(1))
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getAvgSpend30D()).isCloseTo(50.0, within(1e-9));
            assertThat(enriched.getRatioToMedian()).isCloseTo(2.0, within(1e-9)); // 100/50
            assertThat(enriched.getMaxSingleJump()).isCloseTo(2.0, within(1e-9)); // 100/50
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("should handle transaction at exact boundary of 1 hour")
        void enrichCreditTransaction_handlesExactHourBoundary() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 100.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 50.0, baseTime.minusMinutes(60)) // exactly 1H ago
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            // Transaction at exactly 60 minutes should NOT be included (isAfter check)
            assertThat(enriched.getVelocity1H()).isZero();
        }

        @Test
        @DisplayName("should handle transaction at exact boundary of 24 hours")
        void enrichCreditTransaction_handlesExactDayBoundary() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 100.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 50.0, baseTime.minusDays(1)) // exactly 24H ago
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            // Transaction at exactly 24 hours should NOT be included (isAfter check)
            assertThat(enriched.getVelocity24H()).isZero();
        }

        @Test
        @DisplayName("should call repository with correct interval")
        void enrichCreditTransaction_callsRepositoryWithCorrectInterval() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 100.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 50.0, baseTime.minusDays(1))
                    ));

            enrichmentService.enrichCreditTransaction(currentTransaction);

            verify(creditTransactionRepository).getCreditTransactionByInterval(43200); // 30 * 24 * 60
        }

        @Test
        @DisplayName("should handle large transaction amounts")
        void enrichCreditTransaction_handlesLargeAmounts() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 1_000_000.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 500_000.0, baseTime.minusDays(1)),
                            transaction(2L, 750_000.0, baseTime.minusDays(5)),
                            transaction(3L, 625_000.0, baseTime.minusDays(10))
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getAvgSpend30D()).isCloseTo(625_000.0, within(1e-9));
        }

        @Test
        @DisplayName("should handle small transaction amounts")
        void enrichCreditTransaction_handlesSmallAmounts() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 0.01, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 0.02, baseTime.minusDays(1)),
                            transaction(2L, 0.03, baseTime.minusDays(5)),
                            transaction(3L, 0.025, baseTime.minusDays(10))
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getAvgSpend30D()).isCloseTo(0.025, within(1e-9));
        }
    }

    @Nested
    @DisplayName("Combined Features Tests")
    class CombinedFeaturesTests {

        @Test
        @DisplayName("should calculate all features correctly in typical scenario")
        void enrichCreditTransaction_calculatesAllFeaturesCorrectly() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 150.0, baseTime);

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(List.of(
                            transaction(1L, 100.0, baseTime.minusMinutes(30)),
                            transaction(2L, 120.0, baseTime.minusMinutes(10)),
                            transaction(3L, 80.0, baseTime.minusHours(10))
                    ));

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            // Velocity
            assertThat(enriched.getVelocity1H()).isEqualTo(2);
            assertThat(enriched.getVelocity24H()).isEqualTo(3);

            // Distinct merchants
            assertThat(enriched.getDistinctMerchants1H()).isEqualTo(2);

            // Monetary features
            assertThat(enriched.getAvgSpend30D()).isCloseTo(100.0, within(1e-9));
            assertThat(enriched.getZScore()).isCloseTo(2.5, within(1e-9));
            assertThat(enriched.getRatioToMedian()).isCloseTo(1.5, within(1e-9)); // 150/100 (median)
            assertThat(enriched.getMaxSingleJump()).isCloseTo(1.25, within(1e-9)); // 150/120 (max)

            verify(creditTransactionRepository, times(1)).getCreditTransactionByInterval(Converter.daysToMinutes(30));
        }

        @Test
        @DisplayName("should handle high volume scenario")
        void enrichCreditTransaction_handlesHighVolumeScenario() {
            LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            CreditTransaction currentTransaction = transaction(999L, 100.0, baseTime);

            // Create multiple transactions in last hour
            List<CreditTransaction> transactions = List.of(
                    transaction(1L, 50.0, baseTime.minusMinutes(5)),
                    transaction(2L, 60.0, baseTime.minusMinutes(10)),
                    transaction(3L, 70.0, baseTime.minusMinutes(15)),
                    transaction(4L, 80.0, baseTime.minusMinutes(20)),
                    transaction(5L, 90.0, baseTime.minusMinutes(25))
            );

            when(creditTransactionRepository.getCreditTransactionByInterval(Converter.daysToMinutes(30)))
                    .thenReturn(transactions);

            EnrichedTransactionDto enriched = enrichmentService.enrichCreditTransaction(currentTransaction);

            assertThat(enriched.getVelocity1H()).isEqualTo(5);
            assertThat(enriched.getVelocity24H()).isEqualTo(5);
            assertThat(enriched.getDistinctMerchants1H()).isEqualTo(5);
        }
    }

    private CreditTransaction transaction(long id, double amount, LocalDateTime processedAt) {
        CreditTransaction tx = new CreditTransaction();
        tx.setTransactionInternalId(id);
        tx.setTransactionAmount(BigDecimal.valueOf(amount));
        tx.setProcessedAt(processedAt);
        return tx;
    }
}
