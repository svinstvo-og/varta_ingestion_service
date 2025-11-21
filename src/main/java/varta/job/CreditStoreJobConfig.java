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
                .sql("SELECT * FROM credit_store")
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
                log.warn("Failed to process user with external id {}. Reason: {}", raw.toString(), e.getMessage());
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
                        ":terminalId, :acquirerAccountNum, :abnormal, :abnormalStateId)")
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
    public Job CreditStoreJob(
            JobRepository jobRepository,
            Step creditStoreReadProcessWriteStep) {

        return new JobBuilder("CreditStoreJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(creditStoreReadProcessWriteStep)
                .build();
    }

}
