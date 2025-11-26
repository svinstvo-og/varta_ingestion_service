package varta.model.pgsql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import varta.model.mysql.RawFinancialTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Layer 2

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "financial_transaction")
public class FinancialTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionExternalId;
    private String transactionInternalId;

//    private Integer transactionCategory;
    private int cardPanReference;
    private int cardEntryMode;

    private BigDecimal transactionAmount;
    private int currencyCode;
    private LocalDateTime transactionProcessedAt;

    // Not sure
    //private String acquirerCountryCode;
    private int responseCode;

    private BigDecimal feeAmount;


    @ManyToOne(fetch = FetchType.LAZY)
    private CreditCard cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    private CreditStore merchantAcquirerId;

    public FinancialTransaction(RawFinancialTransaction raw) {
        this.transactionInternalId = raw.getTransactionUniqueId();
        this.cardPanReference = Integer.parseInt(raw.getCardPanReference());
        this.cardEntryMode = Integer.parseInt(raw.getTerminalEntryMode());
        this.transactionAmount = raw.getTransactionAmount();
        this.currencyCode = Integer.parseInt(raw.getCurrencyCodeNum());

        // Datetime conversion
        this.transactionProcessedAt = LocalDateTime.of(
                LocalDate.parse(raw.getSettlementDate(), DateTimeFormatter.BASIC_ISO_DATE),
                LocalTime.parse(raw.getTransactionTimestampLocal().substring(3), DateTimeFormatter.ISO_LOCAL_TIME));

        this.responseCode = Integer.parseInt(raw.getResponseCode());
        this.feeAmount = raw.getFeeOrMarkupAmount();
    }
}
