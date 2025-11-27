package varta.job;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import varta.model.mysql.RawFinancialTransaction;
import varta.model.pgsql.FinancialTransaction;

import javax.sql.DataSource;

// Level 2

@Configuration
@EnableBatchProcessing
@Slf4j
public class FinancialTransactionJobConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JdbcCursorItemReader<RawFinancialTransaction> mysqlFinancialTransactionReader(
            @Qualifier("mysqlDataSource" ) DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<RawFinancialTransaction>()
                .name("mysqlFinancialTransactionReader")
                .dataSource(dataSource)
                .sql("SELECT " +
                        // --- 1. Map Raw Columns (F1...F45) ---
                        "ft.id, " +
                        "ft.F1 AS transactionUniqueId, " +
                        "ft.F2 AS cardIdentifier, " +
                        "ft.F3 AS transactionCode, " +
                        "ft.F4 AS transactionCodeDup, " +
                        "ft.F5 AS cardPanReference, " +
                        "ft.F6 AS authorizationCode, " +
                        "ft.F7 AS transactionStatusCode, " +
                        "ft.F8 AS transactionTypeCode, " +
                        "ft.F9 AS cardPresenceFlag, " +
                        "ft.F10 AS transactionAmount, " +
                        "ft.F11 AS transactionAmountDup1, " +
                        "ft.F12 AS transactionDate, " +
                        "ft.F13 AS transactionTime, " +
                        "ft.F14 AS processingDate, " +
                        "ft.F15 AS terminalEntryMode, " +
                        "ft.F16 AS merchantInternalId, " +
                        "ft.F17 AS merchantAcquirerId, " +
                        "ft.F18 AS merchantNameAndRank, " +
                        "ft.F19 AS merchantCategoryCode, " +
                        "ft.F20 AS serviceCode, " +
                        "ft.F21 AS acquirerCountryCode, " +
                        "ft.F22 AS posConditionCode, " +
                        "ft.F23 AS currencyCodeNum, " +
                        "ft.F24 AS countryCodeAlpha, " +
                        "ft.F25 AS settlementDate, " +
                        "ft.F26 AS transactionTimestampLocal, " +
                        "ft.F27 AS responseCode, " +
                        "ft.F28 AS authSourceCode, " +
                        "ft.F29 AS reservedField1, " +
                        "ft.F30 AS systemReferenceId1, " +
                        "ft.F31 AS recordCreationTimestamp, " +
                        "ft.F32 AS recordUpdateTimestamp, " +
                        "ft.F33 AS reversalFlag, " +
                        "ft.F34 AS currencyCodeNumDup, " +
                        "ft.F35 AS settlementAmount, " +
                        "ft.F36 AS currencyCodeNumDup2, " +
                        "ft.F37 AS billingAmount, " +
                        "ft.F38 AS feeOrMarkupAmount, " +
                        "ft.F39 AS systemReferenceId2, " +
                        "ft.F40 AS systemReferenceId3, " +
                        "ft.F41 AS systemReferenceId4, " +
                        "ft.F42 AS systemReferenceId5, " +
                        "ft.F43 AS chargebackFlag, " +
                        "ft.F44 AS transactionAmountDup2, " +
                        "ft.F45 AS currencyCodeNumDup3, " +

                        // --- 2. THE CRITICAL JOINS (Flattening) ---

                        // Get the Stable Card ID (Assuming credit_card.C4 matches F2)
                        // We select 'card_number' (or whatever your stable external ID column is in raw card table)
                        "cc.C4 AS joinedCardExternalId, " +

                        // Get the Stable Merchant ID (Assuming credit_store.S1 matches F17)
                        // We select S18 (MerchantUniqueId)
                        "cs.S18 AS joinedMerchantExternalId " +

                        "FROM credit_f_t ft " +

                        // Left Join Card: Link F2 (CardIdentifier) -> Card.C4 (CardIdentifier)
                        "LEFT JOIN credit_card cc ON ft.F2 = cc.C4 " +

                        // Left Join Store: Link F17 (AcquirerId) -> Store.S1 (AcquirerId)
                        "LEFT JOIN credit_store cs ON ft.F17 = cs.S1")
                .rowMapper(new BeanPropertyRowMapper<>(RawFinancialTransaction.class))
                .fetchSize(1000)
                .rowMapper(new BeanPropertyRowMapper<>(RawFinancialTransaction.class))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<FinancialTransaction> financialTransactionWriter(
            @Qualifier("pgsqlDataSource") DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<FinancialTransaction>()
                .dataSource(dataSource)
                .sql("INSERT INTO financial_transaction (" +
                        "  transaction_external_id, " +
                        "  card_pan_reference, " +
                        "  card_entry_mode, " +
                        "  transaction_amount, " +
                        "  currency_code, " +
                        "  transaction_processed_at, " +
                        "  response_code, " +
                        "  fee_amount, " +
                        "  card_internal_card_id, " +
                        "  merchant_store_internal_id" +
                        ") VALUES (" +
                        "  :transactionExternalId, " +
                        "  :cardPanReference, " +
                        "  :cardEntryMode, " +
                        "  :transactionAmount, " +
                        "  :currencyCode, " +
                        "  :transactionProcessedAt, " +
                        "  :responseCode, " +
                        "  :feeAmount, " +
                        // The Helper Getters
                        "  :cardInternalId, " +      // Looks for getCardInternalId()
                        "  :merchantInternalId" +    // Looks for getMerchantInternalId()
                        ") " +
                        // Idempotency check
                        "ON CONFLICT (transaction_external_id) DO NOTHING")
                .beanMapped()
                .assertUpdates(false)
                .build();
    }

    @Bean
    public Step FinancialTransactionReadProcessWriteStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<RawFinancialTransaction> mysqlFinancialTransactionReader,
            ItemProcessor<RawFinancialTransaction, FinancialTransaction> FinancialTransactionProcessor,
            ItemWriter<FinancialTransaction> pgsqlFinancialTransactionWriter) {

        return new StepBuilder("financialTransactionReadProcessWriteStep", jobRepository)
                .<RawFinancialTransaction, FinancialTransaction>chunk(1000, transactionManager)
                .reader(mysqlFinancialTransactionReader)
                .processor(FinancialTransactionProcessor)
                .writer(pgsqlFinancialTransactionWriter)
                .build();
    }

    @Bean
    public Job financialTransactionJob(
            JobRepository jobRepository,
            Step FinancialTransactionReadProcessWriteStep) {

        return new JobBuilder("financialTransactionJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(FinancialTransactionReadProcessWriteStep)
                .build();
    }
}
