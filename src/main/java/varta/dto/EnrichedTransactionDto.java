package varta.dto;

import java.math.BigDecimal;

public class EnrichedTransactionDto {
    //velocity features
    private Double velocity1H;
    private Double velocity24H;
    private Integer distinctMerchants1H;
    private Double failRatio1H;

    //monetary deviation features
    private Double avgSpend30D;
    private Double zScore;
    private Double ratioToMedian;
    private Double maxSingleJump;

    //temporal features
    private Long secondsSinceLastTransaction;
    private Boolean isNight;
}
