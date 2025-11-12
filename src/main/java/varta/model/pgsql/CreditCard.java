package varta.model.pgsql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import varta.dto.AbnormalState;

import java.util.List;

@Entity
@Table(name = "credit_card")
@Getter
@Setter
public class CreditCard {

    @Id
    private Long internalCardId;
    private String externalCardId;

    // Otherwise is user
    private Boolean isMerchant;

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

    private AbnormalState abnormalState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_user_id")
    private CreditUser creditUser;

    @OneToMany(mappedBy = "transactionInternalId")
    private List<FinancialTransaction> financialTransactions;

    @OneToMany(mappedBy = "sourceCardId")
    private List<CreditTransaction> creditTransactionsOutgoing;

    @OneToMany(mappedBy = "destinationCardId")
    private List<CreditTransaction> creditTransactionsIncoming;
}