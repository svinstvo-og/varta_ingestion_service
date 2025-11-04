package varta.model.pgsql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import varta.dto.AbnormalState;

import java.util.List;

@Entity
@Table(name = "credit_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalUserId;
    private String externalUserId;

    private Integer age;
    private Integer gender;
    private String job;
    private Integer wage;

    @OneToMany(mappedBy = "credit_user")
    private List<CreditCard> cards;

    private Boolean abnormal;
    private AbnormalState abnormalState;

    @Column(name = "loc_id")
    private String locId;
}
