package crm.orchestra.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "credit_card")
@Getter
@Setter
public class CreditCard {
    @Id
    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "owner_type")
    private String ownerType;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "C4")
    private String cardIdentifier;

    @Column(name = "C5")
    private String cardType;

    @Column(name = "C6")
    private String cardProductCode;

    @Column(name = "C7")
    private String cardNickname;

    @Column(name = "C8")
    private String cardFeatureFlag;

    @Column(name = "C9")
    private String locationId;

    @Column(name = "C10")
    private String branchCode;

    @Column(name = "C11")
    private String fullLocationCode;

    private Integer abnormal;

    @Column(name = "abnormal_state", columnDefinition = "JSONB")
    private String abnormalState;
}
