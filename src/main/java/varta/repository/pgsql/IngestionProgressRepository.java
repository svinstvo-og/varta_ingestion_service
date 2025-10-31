package varta.repository.pgsql;

import varta.model.pgsql.IngestionProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngestionProgressRepository extends JpaRepository<IngestionProgress, String> {

}
