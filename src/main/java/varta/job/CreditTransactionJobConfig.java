package varta.job;

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
import varta.model.mysql.RawTransaction;
import varta.model.pgsql.CreditTransaction;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@Slf4j
public class CreditTransactionJobConfig {

    @Bean
    public JdbcCursorItemReader<RawTransaction> creditTransactionReader(@Qualifier("mysqlDataSource") DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<RawTransaction>()
                .name("mysqlCreditTransactionReader")
                .dataSource(dataSource)
                .sql("SELECT " +
                        // --- 1. Map Raw Columns (T1...T39) ---
                        "t.id, " +
                        "t.T1 AS sourceCardIdentifier, " +
                        "t.T2 AS transactionChannelCode, " +
                        "t.T3 AS transactionCode, " +
                        "t.T4 AS transactionPanReference, " +
                        "t.T5 AS reservedField1, " +
                        "t.T6 AS errorFlag, " +
                        "t.T7 AS transactionTypeCode, " +
                        "t.T8 AS completionFlag, " +
                        "t.T9 AS authorizationCode, " +
                        "t.T10 AS transactionTimestampLocal, " +
                        "t.T11 AS partialAuthFlag, " +
                        "t.T12 AS systemTraceId, " +
                        "t.T13 AS transactionCodeDup1, " +
                        "t.T14 AS processingCode, " +
                        "t.T15 AS networkCode, " +
                        "t.T16 AS reversalFlag, " +
                        "t.T17 AS transactionAmount, " +
                        "t.T18 AS transactionStatusCode, " +
                        "t.T19 AS transactionTimeShort, " +
                        "t.T20 AS transactionCodeDup2, " +
                        "t.T21 AS errorCode, " +
                        "t.T22 AS transactionCompositeKey, " +
                        "t.T23 AS transactionDate, " +
                        "t.T24 AS transactionCodeDup3, " +
                        "t.T25 AS merchantAcquirerId, " +
                        "t.T26 AS responseCode, " +
                        "t.T27 AS transactionMode, " +
                        "t.T28 AS entryMode, " +
                        "t.T29 AS reservedField2, " +
                        "t.T30 AS merchantCategoryCode, " +
                        "t.T31 AS merchantInternalId, " +
                        "t.T32 AS transactionDescription, " +
                        "t.T33 AS terminalTypeCode, " +
                        "t.T34 AS terminalIdShort, " +
                        "t.T35 AS cardProductIndicator, " +
                        "t.T36 AS transactionInitiator, " +
                        "t.T37 AS destinationCardIdentifier, " +
                        "t.T38 AS routingFlag, " +
                        "t.T39 AS authenticationFlag, " +
                        "t.abnormal, " +
                        "t.abnormal_state AS abnormalState, " +

                        // --- 2. THE FLATTENED JOINS ---

                        // Get Stable Source Card ID (Link T1 -> C4)
                        "src_cc.C4 AS joinedSourceCardExternalId, " +

                        // Get Stable Destination Card ID (Link T37 -> C4)
                        "dst_cc.C4 AS joinedDestCardExternalId, " +

                        // Get Stable Merchant ID (Link T25 (Acquirer) -> S1)
                        "store.S18 AS joinedMerchantExternalId " +

                        "FROM credit_trans t " +

                        // Join for Source Card
                        "LEFT JOIN credit_card src_cc ON t.T1 = src_cc.C4 " +

                        // Join for Destination Card
                        "LEFT JOIN credit_card dst_cc ON t.T37 = dst_cc.C4 " +

                        // Join for Merchant (Assuming T25 links to S1)
                        "LEFT JOIN credit_store store ON t.T25 = store.S1")
                .rowMapper(new BeanPropertyRowMapper<>(RawTransaction.class))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<CreditTransaction> creditTransactionWriter(
            @Qualifier("pgsqlDataSource") DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<CreditTransaction>()
                .dataSource(dataSource)
                .sql("INSERT INTO credit_trans (" +
                        "  transaction_pan_reference, " +
                        "  is_transfer, " +
                        "  transaction_code, " +
                        "  system_trace_id, " +
                        "  transaction_amount, " +
                        "  transaction_composite_key, " +
                        "  processed_at, " +
                        "  response_code, " +
                        "  entry_mode, " +
                        "  transaction_description, " +
                        "  terminal_type_code, " +
                        "  terminal_id, " +
                        "  authentication_flag, " +
                        "  abnormal, " +
                        "  abnormal_state, " +
                        // Foreign Keys
                        "  source_card_id, " +
                        "  destination_card_id, " +
                        "  merchant_acquirer_id" +
                        ") VALUES (" +
                        "  :transactionPanReference, " +
                        "  :isTransfer, " + // Boolean maps to tinyint/boolean automatically
                        "  :transactionCode, " +
                        "  :systemTraceId, " +
                        "  :transactionAmount, " +
                        "  :transactionCompositeKey, " +
                        "  :processedAt, " +
                        "  :responseCode, " +
                        "  :entryMode, " +
                        "  :transactionDescription, " +
                        "  :terminalTypeCode, " +
                        "  :terminalId, " +
                        "  :authenticationFlag, " +
                        "  :abnormal, " +
                        "  :abnormalStateId, " + // Uses getAbnormalStateId()
                        // Relationships
                        "  :sourceCardInternalId, " +       // Uses getSourceCardInternalId()
                        "  :destinationCardInternalId, " +  // Uses getDestinationCardInternalId()
                        "  :merchantAcquirerInternalId" +   // Uses getMerchantAcquirerInternalId()
                        ") " +
                        // Assuming transaction_pan_reference + composite key might be unique?
                        // If you have a unique key, add ON CONFLICT here.
                        "ON CONFLICT DO NOTHING")
                .beanMapped()
                .assertUpdates(false)
                .build();
    }

    @Bean
    public Step CreditTransactionReadProcessWriteStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<RawTransaction> mysqlCreditTransactionReader,
            ItemProcessor<RawTransaction, CreditTransaction> CreditTransactionProcessor,
            ItemWriter<CreditTransaction> pgsqlCreditTransactionWriter) {

        return new StepBuilder("creditTransactionReadProcessWriteStep", jobRepository)
                .<RawTransaction, CreditTransaction>chunk(1000, transactionManager)
                .reader(mysqlCreditTransactionReader)
                .processor(CreditTransactionProcessor)
                .writer(pgsqlCreditTransactionWriter)
                .build();
    }

    @Bean(name = "creditTransactionJob")
    public Job creditTransactionJob(
            JobRepository jobRepository,
            Step CreditTransactionReadProcessWriteStep) {

        log.info("creditTransactionJob BEAN IS CREATED");
        return new JobBuilder("creditTransactionJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(CreditTransactionReadProcessWriteStep)
                .build();
    }
}
