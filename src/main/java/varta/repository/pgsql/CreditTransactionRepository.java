package varta.repository.pgsql;

import org.hibernate.annotations.processing.SQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import varta.model.pgsql.CreditTransaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, Long> {

    @Query(value = "SELECT * FROM credit_trans WHERE " +
            "processed_at >= NOW() - CAST(:minutes || ' minutes' AS INTERVAL)", nativeQuery = true)
    public List<CreditTransaction> getCreditTransactionByInterval(int minutes);

    Optional<CreditTransaction> findTopBySourceCardInternalCardIdAndProcessedAtLessThanOrderByProcessedAtDesc(Long sourceCardId,
                                                                                                             LocalDateTime processedAt);

}
