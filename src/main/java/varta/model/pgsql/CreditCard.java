package varta.model.pgsql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "credit_card")
@Getter
@Setter
public class CreditCard {

    // TODO: make an enum
    private String ownerType;

    @Column(name = "owner_id")
    private Long ownerId;

    @Id
    private String cardId;

    // A code for the card type. 02 is correlated with fraud flags. Not so sure
    private String cardType;

    // A code identifying the specific card product (e.g., standard, premium)
    private String cardProductCode;

    // An optional, user-defined name for the card.
    private String cardNickname;

    //A flag for a specific card feature. Often null (--). Do some research here as well
    private String cardFeatureFlag;

    // Foreign key linking to credit_user.loc_id.
    // TODO: wtf
    private String locationId;

    // A sub-code for the location, likely a specific branch.
    private String branchCode;

    // A concatenation of C9 and C10.
    private String fullLocationCode;

    private Integer abnormal;

    // TODO: make an enum
    @Column(name = "abnormal_state", columnDefinition = "JSONB")
    private String abnormalState;

    @OneToMany(mappedBy = "credit_card")
    private List<FinancialTransaction> financialTransactions;

    @OneToMany(mappedBy = "credit_card")
    private List<CreditTransaction> creditTransactionsOutgoing;

    @OneToMany(mappedBy = "credit_card")
    private List<CreditTransaction> creditTransactionsIncoming;
}
