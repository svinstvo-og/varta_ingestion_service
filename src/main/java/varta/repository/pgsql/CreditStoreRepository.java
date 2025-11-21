package varta.repository.pgsql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import varta.model.pgsql.CreditStore;

@Repository
public interface CreditStoreRepository extends JpaRepository<CreditStore, Long> {
}
