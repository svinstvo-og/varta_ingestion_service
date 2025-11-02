package varta.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import varta.model.mysql.RawCreditUser;

@Repository
public interface RawCreditUserRepository extends JpaRepository<RawCreditUser, Long> {
}
