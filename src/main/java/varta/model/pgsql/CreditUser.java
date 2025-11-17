package varta.model.pgsql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import varta.dto.AbnormalState;
import varta.model.mysql.RawCreditUser;

import java.util.*;

@Entity
@Table(name = "credit_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class CreditUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalUserId;
    private String externalUserId;

    private Integer age;
    private Integer gender;
    private String job;
    private Integer wage;

    @OneToMany(mappedBy = "creditUser")
    private List<CreditCard> cards;

    private Boolean abnormal;
    private AbnormalState abnormalState;

    @Column(name = "loc_id")
    private String locId;

    public CreditUser(RawCreditUser raw) throws JsonProcessingException {
        this.externalUserId = raw.getUserNo();
        this.age = raw.getAge();
        this.gender = raw.getGender();
        this.job = raw.getJob();
        this.wage = raw.getWage();
        this.abnormal = raw.getAbnormal() == 1;
        this.locId = raw.getLocId();

        // That solution is so ugly, but I love it that way
        this.abnormalState = null;

        Map<String, Integer> abnormalMap = new ObjectMapper().readValue(raw.getAbnormalState(), HashMap.class);
        List<Integer> values = abnormalMap.values().stream().toList();
        for (int i=0; i<abnormalMap.size(); i++) {
             if (values.get(i) == 1) {
                this.abnormalState = Arrays.stream(AbnormalState.values()).toList().get(i);
                 //TEST ONLY
                 log.info("Raw JSON abnormal state for user {} is: {}", raw.getUserNo(), this.getAbnormalState());
                break;
             }
        }
    }
}
