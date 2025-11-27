package varta.model.pgsql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@ToString
@Table(name = "financial_transaction")
public class FinancialTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionInternalId;
    @Column(nullable = false, unique = true)
    private String transactionExternalId;

//    private Integer transactionCategory;
    private long cardPanReference;
    private int cardEntryMode;

    private BigDecimal transactionAmount;
    private int currencyCode;
    private LocalDateTime transactionProcessedAt;

    // Not sure
    //private String acquirerCountryCode;
    private int responseCode;

    private BigDecimal feeAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    private CreditCard card;

    @ManyToOne(fetch = FetchType.LAZY)
    private CreditStore merchant;

    public FinancialTransaction(RawFinancialTransaction raw) {
        this.transactionExternalId = raw.getTransactionUniqueId();
        this.cardPanReference = Long.parseLong(raw.getCardPanReference());
        this.cardEntryMode = Integer.parseInt(raw.getTerminalEntryMode());
        this.transactionAmount = raw.getTransactionAmount();
        this.currencyCode = Integer.parseInt(raw.getCurrencyCodeNum());

        DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE; // YYYYMMDD
        DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HHmmss"); // No colons

        LocalDate datePart = LocalDate.parse(raw.getSettlementDate(), DATE_FMT);
        String timeString = raw.getTransactionTimestampLocal().substring(4);

        this.transactionProcessedAt = LocalDateTime.of(
                datePart,
                LocalTime.parse(timeString, TIME_FMT)
        );
        this.responseCode = Integer.parseInt(raw.getResponseCode());
        this.feeAmount = raw.getFeeOrMarkupAmount();
    }

    public Long getCardInternalId() {
        return (card != null) ? card.getInternalCardId() : null;
    }

    public Long getMerchantInternalId() {
        return (merchant != null) ? merchant.getStoreInternalId() : null;
    }
}
