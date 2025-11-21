package varta.model.pgsql;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import varta.dto.AbnormalState;
import varta.model.mysql.RawCreditStore;
import varta.util.AbnormalStateConverter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "credit_store")
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class CreditStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeInternalId;
    private String storeExternalId;

    private String industry;
    private String name;
    private String rank;

    // TODO: find out what this means
    private String consumptionRange;

    private String openingHours;
    // A sub-identifier for a specific store or terminal.
    private Integer merchantSubId;

    // A short code for the merchant's brand (e.g., qdt, vsi). Not sure what that means
    private Integer merchantBrandCode;

    private Integer merchantCategoryCode;
    private LocalDate registrationDate;

    // No idea what that is rly
    private Integer riskFlag1;
    private Integer riskFlag2;
    private Integer riskFlag3;
    private Integer riskFlag4;

    private String internalCategoryCode;

    // An identifier for the merchant's terminal or POS system.
    private String terminalId;

    //A long numeric string, likely the merchant's account number with the acquirer.
    private String acquirerAccountNum;

    private Boolean abnormal;

    @Nullable
    private AbnormalState abnormalState;

    @OneToMany(mappedBy = "merchantAcquirerId")
    private List<FinancialTransaction> financialTransactions;

    @OneToMany(mappedBy = "merchantAcquirerId")
    private List<CreditTransaction> creditTransactions;

    public CreditStore(RawCreditStore raw) throws JsonProcessingException {
        this.storeExternalId = raw.getMerchantUniqueId();
        this.industry = raw.getIndustry();
        this.name = raw.getName();
        this.rank = raw.getRank();
        this.consumptionRange = raw.getConsumptionRange();
        this.openingHours = raw.getOpeningHours();
        this.internalCategoryCode = raw.getInternalCategoryCode();
        this.terminalId = raw.getTerminalId();
        this.acquirerAccountNum = raw.getAcquirerAccountNum();

        log.info("ABNORMALLLL");
        this.abnormal = raw.getAbnormal() == 1;
        this.abnormalState = AbnormalStateConverter.convertAbnormalState(raw.getAbnormalState());
    }
}