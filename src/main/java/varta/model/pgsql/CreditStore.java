package varta.model.pgsql;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "credit_store")
public class CreditStore {
    @Id
    private Long id;

    private String industry;
    private String name;
    private String rank;

    // TODO: find out what this means
    private String consumptionRange;

    private String openingHours;
    // A sub-identifier for a specific store or terminal.
    private Integer merchantSubId;

    // A short code for the merchant's brand (e.g., qdt, vsi). Not sure what that means
    private Integer merchantBrandCode;

    private Integer merchantCategoryCode;
    private LocalDate registrationDate;

    // No idea what that is rly
    private Integer riskFlag1;
    private Integer riskFlag2;
    private Integer riskFlag3;
    private Integer riskFlag4;

    private String internalCategoryCode;

    // An identifier for the merchant's terminal or POS system.
    private String terminalId;

    //A long numeric string, likely the merchant's account number with the acquirer.
    private String acquirerAccountNum;

    private Boolean abnormal;

    // TODO: make an enum
    @Column(name = "abnormal_state", columnDefinition = "JSONB")
    private String abnormalState;

    @OneToMany(mappedBy = "credit_store")
    private List<FinancialTransaction> financialTransactions;

    @OneToMany(mappedBy = "credit_store")
    private List<CreditTransaction> creditTransactions;
}
