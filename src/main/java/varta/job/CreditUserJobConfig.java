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

@EnableBatchProcessing
@Configuration
@Slf4j
public class CreditUserJobConfig {

    @Bean
    public JdbcCursorItemReader<RawCreditUser> mysqlCreditUserReader(
            @Qualifier("mysqlDataSource" ) DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<RawCreditUser>()
                .name("mysqlCreditUserReader")
                .dataSource(dataSource)
                .sql("SELECT id, age, gender, job, wage, card, abnormal, abnormal_state, user_no, loc_id FROM credit_user")
                .rowMapper(new BeanPropertyRowMapper<>(RawCreditUser.class))
                .fetchSize(1000)
                .rowMapper(new BeanPropertyRowMapper<>(RawCreditUser.class))
                .build();
    }

    @Bean
    public ItemProcessor<RawCreditUser, CreditUser> creditUserProcessor() {
        return raw -> {
            try {
                log.info("New credit user: {}", raw.getUserNo());
                CreditUser creditUser = new CreditUser(raw);
                log.info(creditUser.toString());
                return creditUser;
            } catch (JsonProcessingException e) {
                log.warn("Failed to process user with external id {}. Reason: {}", raw.getUserNo(), e.getMessage());
                return null;
            }
        };
    }

    @Bean
    public JdbcBatchItemWriter<CreditUser> pgsqlCreditUserWriter(
            @Qualifier("pgsqlDataSource") DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<CreditUser>()
                .dataSource(dataSource)
                .sql("INSERT INTO credit_user " +
                        "(abnormal, abnormal_state, age, external_user_id, gender, job, loc_id, wage) " +
                        "VALUES " +
                        "(:abnormal, :abnormalStateId, :age, :externalUserId, :gender, :job, :locId, :wage)")
                .beanMapped()
                .build();
    }

    @Bean
    public Step creditUserReadProcessWriteStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<RawCreditUser> mysqlCreditUserReader,
            ItemProcessor<RawCreditUser, CreditUser> creditUserProcessor,
            ItemWriter<CreditUser> pgsqlCreditUserWriter) {

        return new StepBuilder("creditUserReadProcessWriteStep", jobRepository)
                .<RawCreditUser, CreditUser>chunk(1000, transactionManager)
                .reader(mysqlCreditUserReader)
                .processor(creditUserProcessor)
                .writer(pgsqlCreditUserWriter)
                .build();
    }

    @Bean
    public Job creditUserJob(
            JobRepository jobRepository,
            Step creditUserReadProcessWriteStep) {

        return new JobBuilder("creditUserJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(creditUserReadProcessWriteStep)
                .build();
    }
}
