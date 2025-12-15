package varta.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EnrichedTransactionDto {
    // contextual velocity features
    private int velocity1H;
    private int velocity24H;
    private int distinctMerchants1H;

    // contextual monetary deviation features
    private Double avgSpend30D;
    private Double zScore;
    private Double ratioToMedian;
    private Double maxSingleJump;

    // contextual temporal features
    private Long secondsSinceLastTransaction;
    private Boolean isNight;

    // transaction features
    private double amount;

}
