package crm.orchestra.repository;

import crm.orchestra.model.IngestionProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngestionProgressRepository extends JpaRepository<IngestionProgress, String> {

}
