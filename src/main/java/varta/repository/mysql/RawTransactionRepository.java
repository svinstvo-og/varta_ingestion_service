package varta.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import varta.model.mysql.RawTransaction;

@Repository
public interface RawTransactionRepository extends JpaRepository<RawTransaction, Long> {
}
