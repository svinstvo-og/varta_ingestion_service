package crm.orchestra.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.context.annotation.Primary;

@Entity
public class IngestionProgress {
    @Id
    String datasetName;
    Integer lastProcessedId;
}
