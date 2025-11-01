package varta.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import varta.model.mysql.RawFinancialTransaction;

@Repository
public interface RawFinancialTransactionRepository extends JpaRepository<RawFinancialTransaction, Long> {
    //public RawFinancialTransaction findById(Long id);
}
