package crm.orchestra.repository;

import crm.orchestra.model.TransactionRaw6m;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRaw6mRepository extends JpaRepository<TransactionRaw6m, Integer> {
}
