package crm.orchestra.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "credit_store")
public class CreditStore {
    @Id
    private Long id;

    private String industry;

    @Column(name = "name_")
    private String name;

    @Column(name = "rank_")
    private String rank;

    @Column(name = "consumption_range")
    private String consumptionRange;

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(name = "S1")
    private String merchantAcquirerId;

    @Column(name = "S2")
    private String merchantSubId;

    @Column(name = "S3")
    private String merchantBrandCode;

    @Column(name = "S4")
    private String merchantBrandCodeDup;

    @Column(name = "S5")
    private String merchantCategoryCode;

    @Column(name = "S6")
    private String reservedField1;

    @Column(name = "S7")
    private String registrationDate;

    @Column(name = "S8")
    private String statusCode;

    @Column(name = "S9")
    private Integer riskFlag1;

    @Column(name = "S10")
    private Integer riskFlag2;

    @Column(name = "S11")
    private Integer riskFlag3;

    @Column(name = "S12")
    private Integer riskFlag4;

    @Column(name = "S13")
    private String internalCategoryCode;

    @Column(name = "S14")
    private Integer configurationFlag1;

    @Column(name = "S15")
    private Integer configurationFlag2;

    @Column(name = "S16")
    private Integer configurationFlag3;

    @Column(name = "S17")
    private Integer configurationFlag4;

    @Column(name = "S18")
    private String merchantUniqueId;

    @Column(name = "S19")
    private String terminalId;

    @Column(name = "S20")
    private String acquirerAccountNum;

    @Column(name = "S21")
    private String systemReferenceId1;

    @Column(name = "S22")
    private String systemReferenceId2;

    @Column(name = "S23", columnDefinition = "TEXT")
    private String systemReferenceId3;

    @Column(name = "S24")
    private String systemReferenceId4;

    @Column(name = "S25")
    private String systemReferenceId5;

    @Column(name = "S26")
    private String systemReferenceId6;

    @Column(name = "S27")
    private String systemReferenceId7;

    @Column(name = "S28")
    private String systemReferenceId8;

    @Column(name = "S29")
    private String systemReferenceId9;

    @Column(name = "S30")
    private Integer activationFlag;

    private Integer abnormal;

    @Column(name = "abnormal_state", columnDefinition = "JSONB")
    private String abnormalState;
}
