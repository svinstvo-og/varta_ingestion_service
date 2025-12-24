package varta.model.pgsql;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import varta.dto.AbnormalState;
import varta.model.mysql.RawCreditCard;
import varta.util.AbnormalStateConverter;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "credit_card")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalCardId;

    @Column(nullable = false, unique = true)
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

    // TODO CHANGE TO BOOLEAN ONCE ETL IS COMPLETE
    private int abnormal;

    private AbnormalState abnormalState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_user_id")
    private CreditUser creditUser;

    @OneToMany(mappedBy = "card")
    private List<FinancialTransaction> financialTransactions;

    @OneToMany(mappedBy = "sourceCard")
    private List<CreditTransaction> creditTransactionsOutgoing;

    @OneToMany(mappedBy = "destinationCard")
    private List<CreditTransaction> creditTransactionsIncoming;

    public CreditCard(RawCreditCard raw) throws JsonProcessingException {
        log.debug("Creating CreditCard entity from raw credit card - {}", raw.toString());
        this.externalCardId = raw.getCardIdentifier();
        this.isMerchant = Objects.equals(raw.getOwnerType(), "Merchant");
        this.cardType = raw.getCardType();
        this.cardProductCode = raw.getCardProductCode();
        this.cardNickname = raw.getCardNickname();
        this.cardFeatureFlag = raw.getCardFeatureFlag();
        this.locationId = raw.getLocationId();
        this.branchCode = raw.getBranchCode();
        this.fullLocationCode = raw.getFullLocationCode();
        // todo == 1;
        this.abnormal = raw.getAbnormal();
        this.abnormalState = AbnormalStateConverter.convertAbnormalState(raw.getAbnormalState());
    }

    public Integer getAbnormalStateId() {
        return AbnormalStateConverter.getAbnormalStateId(abnormalState);
    }

    public Long getCreditUserId() {
        return (creditUser != null) ? creditUser.getInternalUserId() : null;
    }
}