package varta.job;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import varta.model.mysql.RawCreditStore;
import varta.model.pgsql.CreditStore;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@Slf4j
public class CreditStoreJobConfig {
    @Bean
    public JdbcCursorItemReader<RawCreditStore> mysqlCreditStoreReader(
            @Qualifier("mysqlDataSource" ) DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<RawCreditStore>()
                .name("mysqlCreditStoreReader")
                .dataSource(dataSource)
                // Ugly af, but this is the fastest way performance-wise.
                // Jdbc doesnt understend Jpa anotations like @Column, which I need big time
                .sql("SELECT " +
                        "id, " +
                        "industry, " +
                        "name_ AS `name`, " +
                        "rank_ AS `rank`, " +
                        "consumption_range AS consumptionRange, " +
                        "opening_hours AS openingHours, " +
                        "S1 AS merchantAcquirerId, " +
                        "S2 AS merchantSubId, " +
                        "S3 AS merchantBrandCode, " +
                        "S4 AS merchantBrandCodeDup, " +
                        "S5 AS merchantCategoryCode, " +
                        "S6 AS reservedField1, " +
                        "S7 AS registrationDate, " +
                        "S8 AS statusCode, " +
                        "S9 AS riskFlag1, " +
                        "S10 AS riskFlag2, " +
                        "S11 AS riskFlag3, " +
                        "S12 AS riskFlag4, " +
                        "S13 AS internalCategoryCode, " +
                        "S14 AS configurationFlag1, " +
                        "S15 AS configurationFlag2, " +
                        "S16 AS configurationFlag3, " +
                        "S17 AS configurationFlag4, " +
                        "S18 AS merchantUniqueId, " +
                        "S19 AS terminalId, " +
                        "S20 AS acquirerAccountNum, " +
                        "S21 AS systemReferenceId1, " +
                        "S22 AS systemReferenceId2, " +
                        "S23 AS systemReferenceId3, " +
                        "S24 AS systemReferenceId4, " +
                        "S25 AS systemReferenceId5, " +
                        "S26 AS systemReferenceId6, " +
                        "S27 AS systemReferenceId7, " +
                        "S28 AS systemReferenceId8, " +
                        "S29 AS systemReferenceId9, " +

                        "S30 AS activationFlag, " +
                        "abnormal, " +
                        "abnormal_state AS abnormalState " +

                        "FROM credit_store")
                .rowMapper(new BeanPropertyRowMapper<>(RawCreditStore.class))
                .fetchSize(1000)
                .rowMapper(new BeanPropertyRowMapper<>(RawCreditStore.class))
                .build();
    }

    @Bean
    public ItemProcessor<RawCreditStore, CreditStore> CreditStoreProcessor() {
        return raw -> {
            try {
                log.info("New credit store entity: {}", raw.toString());
                CreditStore CreditStore = new CreditStore(raw);
                log.info(CreditStore.toString());
                return CreditStore;
            } catch (JsonProcessingException e) {
                log.warn("Failed to process store with external id {}. Reason: {}", raw.toString(), e.getMessage());
                return null;
            }
        };
    }

    @Bean
    public JdbcBatchItemWriter<CreditStore> pgsqlCreditStoreWriter(
            @Qualifier("pgsqlDataSource") DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<CreditStore>()
                .dataSource(dataSource)
                .sql("INSERT INTO credit_store " +
                        "(store_external_id, industry, name, rank, consumption_range, opening_hours, " +
                        "merchant_sub_id, merchant_brand_code, merchant_category_code, registration_date, " +
                        "risk_flag1, risk_flag2, risk_flag3, risk_flag4, internal_category_code, " +
                        "terminal_id, acquirer_account_num, abnormal, abnormal_state) " +
                        "VALUES " +
                        "(:storeExternalId, :industry, :name, :rank, :consumptionRange, :openingHours, " +
                        ":merchantSubId, :merchantBrandCode, :merchantCategoryCode, :registrationDate, " +
                        ":riskFlag1, :riskFlag2, :riskFlag3, :riskFlag4, :internalCategoryCode, " +
                        ":terminalId, :acquirerAccountNum, :abnormal, :abnormalStateId)" +
                        "ON CONFLICT (store_external_id) DO NOTHING")
                .assertUpdates(false)
                .beanMapped()
                .build();
    }

    @Bean
    public Step creditStoreReadProcessWriteStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<RawCreditStore> mysqlCreditStoreReader,
            ItemProcessor<RawCreditStore, CreditStore> CreditStoreProcessor,
            ItemWriter<CreditStore> pgsqlCreditStoreWriter) {

        return new StepBuilder("creditStoreReadProcessWriteStep", jobRepository)
                .<RawCreditStore, CreditStore>chunk(1000, transactionManager)
                .reader(mysqlCreditStoreReader)
                .processor(CreditStoreProcessor)
                .writer(pgsqlCreditStoreWriter)
                .build();
    }

    @Bean
    public Job creditStoreJob(
            JobRepository jobRepository,
            Step creditStoreReadProcessWriteStep) {

        return new JobBuilder("creditStoreJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(creditStoreReadProcessWriteStep)
                .build();
    }

}
