package varta.repository.pgsql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import varta.model.pgsql.CreditUser;

@Repository
public interface CreditUserRepository extends JpaRepository<CreditUser, Long> {

}
