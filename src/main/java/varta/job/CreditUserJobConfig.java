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
import varta.model.mysql.RawCreditUser;
import varta.model.pgsql.CreditUser;

import javax.sql.DataSource;

// Layer 0

@Configuration
@EnableBatchProcessing
@Slf4j
public class CreditUserJobConfig {

    @Bean
    public JdbcCursorItemReader<RawCreditUser> mysqlReader(
            @Qualifier("mysqlDataSource" ) DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<RawCreditUser>()
                .name("mysqlReader")
                .dataSource(dataSource)
                .sql("SELECT id, id FROM credit_user")
                .rowMapper(new BeanPropertyRowMapper<>(RawCreditUser.class))
                .build();
    }

    @Bean
    public ItemProcessor<RawCreditUser, CreditUser> creditUserProcessor() {
        return raw -> {
            try {
                log.info("New credit user: {}", raw.getUserNo());
                return new CreditUser(raw);
            } catch (JsonProcessingException e) {
                log.warn("Failed to process user with external id {}. Reason: {}", raw.getUserNo(), e.getMessage());
                return null;
            }
        };
    }

    @Bean
    public JdbcBatchItemWriter<CreditUser> pgsqlWriter(
            @Qualifier("pgsqlDataSource") DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<CreditUser>()
                .dataSource(dataSource)
                .sql("INSERT INTO credit_user (id, processed_info) VALUES (:id, :processedInfo)")
                .beanMapped()
                .build();
    }

    @Bean
    public Step readProcessWriteStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<RawCreditUser> mysqlReader,
            ItemProcessor<RawCreditUser, CreditUser> creditUserProcessor,
            ItemWriter<CreditUser> pgsqlWriter) {

        return new StepBuilder("readProcessWriteStep", jobRepository)
                .<RawCreditUser, CreditUser>chunk(100, transactionManager)
                .reader(mysqlReader)
                .processor(creditUserProcessor)
                .writer(pgsqlWriter)
                .build();
    }

    @Bean
    public Job myEtlJob(
            JobRepository jobRepository,
            Step readProcessWriteStep) {

        return new JobBuilder("myEtlJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(readProcessWriteStep)
                .build();
    }
}
