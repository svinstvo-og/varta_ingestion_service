package varta.job;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import varta.model.mysql.RawCreditStore;
import varta.model.mysql.RawCreditCard;
import varta.model.mysql.RawCreditCard;
import varta.model.pgsql.CreditCard;
import varta.model.pgsql.CreditCard;
import varta.model.pgsql.CreditStore;
import varta.model.pgsql.CreditUser;

import javax.sql.DataSource;

// Level 1

@Configuration
@EnableBatchProcessing
@Slf4j
public class CreditCardJobConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JdbcCursorItemReader<RawCreditCard> mysqlCreditCardReader(
            @Qualifier("mysqlDataSource" ) DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<RawCreditCard>()
                .name("mysqlCreditCardReader")
                .dataSource(dataSource)
                .sql("SELECT " +
                        "card.card_id AS cardId, " +
                        "card.owner_type AS ownerType, " +
                        "card.owner_id AS ownerId, " +
                        "card.C4 AS cardIdentifier, " +
                        "card.C5 AS cardType, " +
                        "card.C6 AS cardProductCode, " +
                        "card.C7 AS cardNickname, " +
                        "card.C8 AS cardFeatureFlag, " +
                        "card.C9 AS locationId, " +
                        "card.C10 AS branchCode, " +
                        "card.C11 AS fullLocationCode, " +
                        "card.abnormal, " +
                        "card.abnormal_state AS abnormalState, " + // Added missing comma

                        // The lookup:
                        "u.user_no AS ownerStableId " +

                        "FROM credit_card card " + // Corrected: Reading from CARD table

                        // We join on the Integer ID, even without a formal FK constraint
                        "LEFT JOIN credit_user u ON card.owner_id = u.id")
                .rowMapper(new BeanPropertyRowMapper<>(RawCreditCard.class))
                .fetchSize(1000)
                .rowMapper(new BeanPropertyRowMapper<>(RawCreditCard.class))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<CreditCard> creditCardWriter(
            @Qualifier("pgsqlDataSource") DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<CreditCard>()
                .dataSource(dataSource)
                .sql("INSERT INTO credit_card (" +
                        "  external_card_id, " +
                        "  is_merchant, " +
                        "  card_type, " +
                        "  card_product_code, " +
                        "  card_nickname, " +
                        "  card_feature_flag, " +
                        "  location_id, " +
                        "  branch_code, " +
                        "  full_location_code, " +
                        "  abnormal, " +
                        "  abnormal_state, " +
                        "  credit_user_id" + // FK
                        ") VALUES (" +
                        "  :externalCardId, " +
                        "  :isMerchant, " +
                        "  :cardType, " +
                        "  :cardProductCode, " +
                        "  :cardNickname, " +
                        "  :cardFeatureFlag, " +
                        "  :locationId, " +
                        "  :branchCode, " +
                        "  :fullLocationCode, " +
                        "  :abnormal, " +
                        "  :abnormalStateId, " +
                        "  :creditUserId" +
                        ") " +
                        "ON CONFLICT (external_card_id) DO NOTHING")
                .beanMapped()
                .assertUpdates(false)
                .build();
    }

    @Bean
    public Step creditCardReadProcessWriteStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<RawCreditCard> mysqlCreditCardReader,
            ItemProcessor<RawCreditCard, CreditCard> CreditCardProcessor,
            ItemWriter<CreditCard> pgsqlCreditCardWriter) {

        return new StepBuilder("creditCardReadProcessWriteStep", jobRepository)
                .<RawCreditCard, CreditCard>chunk(1000, transactionManager)
                .reader(mysqlCreditCardReader)
                .processor(CreditCardProcessor)
                .writer(pgsqlCreditCardWriter)
                .build();
    }

    @Bean
    public Job creditCardJob(
            JobRepository jobRepository,
            Step creditCardReadProcessWriteStep) {

        return new JobBuilder("creditCardJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(creditCardReadProcessWriteStep)
                .build();
    }

}
