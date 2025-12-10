package varta.model.pgsql;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Getter
@Setter
public class CreditStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeInternalId;

    @Column(nullable = false, unique = true)
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

    private String internalCategoryCode;

    // An identifier for the merchant's terminal or POS system.
    private String terminalId;

    //A long numeric string, likely the merchant's account number with the acquirer.
    private String acquirerAccountNum;

    private Boolean abnormal;

    @Nullable
    private AbnormalState abnormalState;

    @OneToMany(mappedBy = "merchant")
    private List<FinancialTransaction> financialTransactions;

    @OneToMany(mappedBy = "merchantAcquirer")
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

        this.abnormal = raw.getAbnormal() == 1;
        this.abnormalState = AbnormalStateConverter.convertAbnormalState(raw.getAbnormalState());
    }

    public Integer getAbnormalStateId() {
        return AbnormalStateConverter.getAbnormalStateId(abnormalState);
    }
}