package varta.repository.pgsql;

import varta.model.pgsql.Sixm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRaw6mRepository extends JpaRepository<Sixm, Integer> {
}
