package varta.model.pgsql;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import varta.dto.AbnormalState;
import varta.model.mysql.RawTransaction;
import varta.util.AbnormalStateConverter;
import varta.util.TimeConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "credit_trans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreditTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionInternalId;
    private String transactionPanReference;

    // Code for the transaction channel. 01 for purchases, 03 for transfers.
    // !isTransfer = purchase
    private Boolean isTransfer;

    // IDK
    private long transactionCode;

    // An 8-digit system trace or audit number.
    private int systemTraceId;

    private BigDecimal transactionAmount;

    // TODO: find out tf this is
    private String transactionCompositeKey;

    private LocalDateTime processedAt;

    // TODO: not yet sure what is that either
    private int responseCode;

    // A code for the transaction entry mode (e.g., 01 for keyed, 07 for contactless).
    private Integer entryMode;

    private String transactionDescription;

    // Those two are sussy aswell, do some digging
    private Integer terminalTypeCode;
    private Integer terminalId;

    // A code indicating who initiated the transaction (e.g., cardholder, merchant).
//    private String transactionInitiator;

    // A flag, consistently 1 or 0, possibly indicating if authentication (e.g., PIN) was performed.
    private Integer authenticationFlag;

    private boolean abnormal;
    private AbnormalState abnormalState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_card_id")
    private CreditCard sourceCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_card_id")
    private CreditCard destinationCard;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_acquirer_id")
    private CreditStore merchantAcquirer;

    public Long getSourceCardInternalId() {
        return (sourceCard != null) ? sourceCard.getInternalCardId() : null;
    }

    public Long getDestinationCardInternalId() {
        return (destinationCard != null) ? destinationCard.getInternalCardId() : null;
    }

    public Long getMerchantAcquirerInternalId() {
        return (merchantAcquirer != null) ? merchantAcquirer.getStoreInternalId() : null;
    }

    // 4. Abnormal State ID
    public Integer getAbnormalStateId() {
        return (abnormalState != null) ? abnormalState.ordinal() : null;
    }

    public CreditTransaction(RawTransaction raw) throws JsonProcessingException {
        this.transactionPanReference = raw.getTransactionPanReference();
        this.isTransfer = Objects.equals(raw.getTransactionChannelCode(), "03");
        this.transactionCode = Long.parseLong(raw.getTransactionCode());
        this.systemTraceId = Integer.parseInt(raw.getSystemTraceId());

        this.transactionAmount = raw.getTransactionAmount();
        this.transactionCompositeKey = raw.getTransactionCompositeKey();

        this.responseCode = Integer.parseInt(raw.getResponseCode());
        this.entryMode = Integer.parseInt(raw.getEntryMode());
        this.transactionDescription = raw.getTransactionDescription();
        this.terminalTypeCode = Integer.parseInt(raw.getTerminalTypeCode());
        this.terminalId = Integer.parseInt(raw.getTerminalIdShort());

        this.processedAt = TimeConverter.convertTimestamp(raw.getTransactionDate(),
                raw.getTransactionTimestampLocal());

        this.authenticationFlag = raw.getAuthenticationFlag();
        this.abnormal = raw.getAbnormal() == 1;
        this.abnormalState = AbnormalStateConverter.convertAbnormalState(raw.getAbnormalState());
    }
}
