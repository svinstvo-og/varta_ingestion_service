package varta.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import varta.model.mysql.RawCreditStore;

public interface RawCreditStoreRepository extends JpaRepository<RawCreditStore, Long> {
}
