package varta.model.pgsql;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import varta.dto.AbnormalState;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_trans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionInternalId;
    private String transactionExternalId;

    // Code for the transaction channel. 01 for purchases, 03 for transfers.
    private Integer transactionChannelCode;

    // IDK
    private String transactionCode;
    private String transactionPan;

    // An 8-digit system trace or audit number.
    private String systemTraceId;

    // TODO: Do some digging
    private String networkCode;

    private BigDecimal transactionAmount;

    // TODO: find out tf this is
    private String transactionCompositeKey;

    private LocalDateTime proccessedAt;

    // TODO: not yet sure what is that either
    private Integer responseCode;

    // A code for the transaction entry mode (e.g., 01 for keyed, 07 for contactless).
    private Integer entryMode;

    // The 4-digit MCC. Is 0000 for transfers.
    private Integer merchantCategoryCode;

    private String transactionDescription;

    // Those two are sussy aswell, do some digging
    private Integer terminalTypeCode;
    private Integer terminalId;

    // A code indicating the card product. Not sure what exactly that means
    private String cardProductIndicator;

    // A code indicating who initiated the transaction (e.g., cardholder, merchant).
    private String transactionInitiator;

    // A flag, consistently 1 or 0, possibly indicating if authentication (e.g., PIN) was performed.
    private Integer authenticationFlag;

    private Integer abnormal;
    private AbnormalState abnormalState;

    @ManyToOne(fetch = FetchType.LAZY)
    private CreditCard sourceCardId;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "destination_card_id")
    private CreditCard destinationCardId;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "destination_card_id")
    private CreditStore merchantAcquirerId;
}
