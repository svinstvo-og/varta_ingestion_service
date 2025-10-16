package crm.orchestra.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.context.annotation.Primary;

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
