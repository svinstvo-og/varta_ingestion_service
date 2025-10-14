package crm.orchestra.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRaw6m {
    @Id
    private Integer id;
    private int step;
    private long amount;
    private String nameOrig;
    private long oldbalanceOrg;
    private long newbalanceOrig;
    private String nameDest;
    private long oldbalanceDest;
    private long newbalanceDest;
    private boolean isFraud;
    private boolean isFlaggedFraud;
}
