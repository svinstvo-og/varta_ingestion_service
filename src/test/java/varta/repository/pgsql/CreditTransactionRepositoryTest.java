package varta.repository.pgsql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import varta.config.PgsqlDataSourceConfig;
import varta.model.pgsql.CreditTransaction;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PgsqlDataSourceConfig.class)
public class CreditTransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CreditTransactionRepository creditTransactionRepository;

    @Test
    public void testGetCreditTransactionByInterval() {
        // given
        CreditTransaction transaction1 = new CreditTransaction();
        transaction1.setProcessedAt(LocalDateTime.now().minusMinutes(5));
        entityManager.persist(transaction1);

        CreditTransaction transaction2 = new CreditTransaction();
        transaction2.setProcessedAt(LocalDateTime.now().minusMinutes(15));
        entityManager.persist(transaction2);

        CreditTransaction transaction3 = new CreditTransaction();
        transaction3.setProcessedAt(LocalDateTime.now().minusMinutes(25));
        entityManager.persist(transaction3);

        entityManager.flush();

        // when
        List<CreditTransaction> foundTransactions = creditTransactionRepository.getCreditTransactionByInterval(20);

        // then
        assertThat(foundTransactions).hasSize(2).contains(transaction1, transaction2);
    }
}
