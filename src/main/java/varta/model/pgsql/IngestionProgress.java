package varta.model.pgsql;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngestionProgress {
    @Id
    String datasetName;
    Integer lastProcessedId;
}
