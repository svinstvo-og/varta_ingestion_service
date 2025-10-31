package varta.model.pgsql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "credit_trans")
public class CreditTransaction {
    @Id
    private Long id;

    @Column(name = "T1")
    private String sourceCardIdentifier;

    @Column(name = "T2")
    private String transactionChannelCode;

    @Column(name = "T3")
    private String transactionCode;

    @Column(name = "T4")
    private String transactionPanReference;

    @Column(name = "T5")
    private String reservedField1;

    @Column(name = "T6")
    private Integer errorFlag;

    @Column(name = "T7")
    private String transactionTypeCode;

    @Column(name = "T8")
    private Integer completionFlag;

    @Column(name = "T9")
    private String authorizationCode;

    @Column(name = "T10")
    private String transactionTimestampLocal;

    @Column(name = "T11")
    private Integer partialAuthFlag;

    @Column(name = "T12")
    private String systemTraceId;

    @Column(name = "T13")
    private String transactionCodeDup1;

    @Column(name = "T14")
    private String processingCode;

    @Column(name = "T15")
    private String networkCode;

    @Column(name = "T16")
    private Integer reversalFlag;

    @Column(name = "T17")
    private BigDecimal transactionAmount;

    @Column(name = "T18")
    private String transactionStatusCode;

    @Column(name = "T19")
    private String transactionTimeShort;

    @Column(name = "T20")
    private String transactionCodeDup2;

    @Column(name = "T21")
    private String errorCode;

    @Column(name = "T22", columnDefinition = "TEXT")
    private String transactionCompositeKey;

    @Column(name = "T23")
    private String transactionDate;

    @Column(name = "T24")
    private String transactionCodeDup3;

    @Column(name = "T25")
    private String merchantAcquirerId;

    @Column(name = "T26")
    private String responseCode;

    @Column(name = "T27")
    private String transactionMode;

    @Column(name = "T28")
    private String entryMode;

    @Column(name = "T29")
    private String reservedField2;

    @Column(name = "T30")
    private String merchantCategoryCode;

    @Column(name = "T31")
    private String merchantInternalId;

    @Column(name = "T32")
    private String transactionDescription;

    @Column(name = "T33")
    private String terminalTypeCode;

    @Column(name = "T34")
    private String terminalIdShort;

    @Column(name = "T35")
    private String cardProductIndicator;

    @Column(name = "T36")
    private String transactionInitiator;

    @Column(name = "T37")
    private String destinationCardIdentifier;

    @Column(name = "T38")
    private Integer routingFlag;

    @Column(name = "T39")
    private Integer authenticationFlag;

    private Integer abnormal;

    @Column(name = "abnormal_state", columnDefinition = "JSONB")
    private String abnormalState;
}
