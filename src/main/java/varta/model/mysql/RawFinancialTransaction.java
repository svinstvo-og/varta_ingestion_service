package varta.model.mysql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_f_t")
public class RawFinancialTransaction {

    @Id
    private Long id;

    @Column(name = "F1")
    private String transactionUniqueId;

    @Column(name = "F2")
    private String cardIdentifier;

    // An 8-digit code categorizing the transaction. Identical to F4. Not sure if I rly need this
    @Column(name = "F3")
    private String transactionCode;

    @Column(name = "F4")
    private String transactionCodeDup;

    // A 12-digit number, likely a tokenized or hashed representation of the card's Primary Account Number (PAN).
    @Column(name = "F5")
    private String cardPanReference;

    @Column(name = "F6")
    private String authorizationCode;

    // Always(?) 00, which is approved
    @Column(name = "F7")
    private String transactionStatusCode;

    // A code for the transaction type. The value is consistently 01, likely indicating 'Purchase'.
    @Column(name = "F8")
    private String transactionTypeCode;

    // A flag indicating card presence. The value is 01, likely for 'Card Present'.
    @Column(name = "F9")
    private String cardPresenceFlag;

    @Column(name = "F10")
    private BigDecimal transactionAmount;

    @Column(name = "F11")
    private BigDecimal transactionAmountDup1;

    @Column(name = "F12")
    private String transactionDate;

    @Column(name = "F13")
    private String transactionTime;

    @Column(name = "F14")
    private String processingDate;

    // A 3-digit code likely indicating how the card was entered (e.g., swiped, inserted, contactless).
    @Column(name = "F15")
    private String terminalEntryMode;

    @Column(name = "F16")
    private String merchantInternalId;

    @Column(name = "F17")
    private String merchantAcquirerId;

    @Column(name = "F18")
    private String merchantNameAndRank;

    @Column(name = "F19")
    private String merchantCategoryCode;

    @Column(name = "F20")
    private String serviceCode;

    @Column(name = "F21")
    private String acquirerCountryCode;

    @Column(name = "F22")
    private String posConditionCode;

    @Column(name = "F23")
    private String currencyCodeNum;

    @Column(name = "F24")
    private String countryCodeAlpha;

    @Column(name = "F25")
    private String settlementDate;

    @Column(name = "F26")
    private String transactionTimestampLocal;

    @Column(name = "F27")
    private String responseCode;

    @Column(name = "F28")
    private String authSourceCode;

    @Column(name = "F29")
    private String reservedField1;

    @Column(name = "F30")
    private String systemReferenceId1;

    @Column(name = "F31")
    private LocalDateTime recordCreationTimestamp;

    @Column(name = "F32")
    private LocalDateTime recordUpdateTimestamp;

    @Column(name = "F33")
    private Integer reversalFlag;

    @Column(name = "F34")
    private String currencyCodeNumDup;

    @Column(name = "F35")
    private BigDecimal settlementAmount;

    @Column(name = "F36")
    private String currencyCodeNumDup2;

    @Column(name = "F37")
    private BigDecimal billingAmount;

    @Column(name = "F38")
    private BigDecimal feeOrMarkupAmount;

    @Column(name = "F39")
    private String systemReferenceId2;

    @Column(name = "F40")
    private String systemReferenceId3;

    @Column(name = "F41")
    private String systemReferenceId4;

    @Column(name = "F42")
    private String systemReferenceId5;

    @Column(name = "F43")
    private Integer chargebackFlag;

    @Column(name = "F44")
    private BigDecimal transactionAmountDup2;

    @Column(name = "F45")
    private String currencyCodeNumDup3;

    // We will fetch credit_card.card_number here
    private String joinedCardExternalId;

    // We will fetch credit_store.S18 (MerchantUniqueId) here
    private String joinedMerchantExternalId;
}
