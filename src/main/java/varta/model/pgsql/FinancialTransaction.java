package varta.model.pgsql;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "financial_transaction")
public class FinancialTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionExternalId;
    private String transactionInternalId;

    private Integer transactionCategory;
    private Integer CardPanReference;
    private Integer CardEntryMode;

    private BigDecimal transactionAmount;
    private Integer currencyCode;
    private LocalDateTime transactionProccessedAt;

    // Not sure
    private String acquirerCountryCode;
    private String responseCode;

    private BigDecimal feeAmount;


    @ManyToOne(fetch = FetchType.LAZY)
    private CreditCard cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    private CreditStore merchantAcquirerId;
}
