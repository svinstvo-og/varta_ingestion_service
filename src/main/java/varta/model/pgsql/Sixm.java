package varta.model.pgsql;

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
public class Sixm {
    @Id
    private Integer id;
    private int step;
    private double amount;
    private String nameOrig;
    private long oldBalanceOrg;
    private long newBalanceOrig;
    private String nameDest;
    private long oldBalanceDest;
    private long newBalanceDest;
    private boolean isFraud;
    private boolean isFlaggedFraud;
}
