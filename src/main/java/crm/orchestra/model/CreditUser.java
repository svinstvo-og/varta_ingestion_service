package crm.orchestra.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "credit_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditUser {
    @Id
    private Long id;

    private Integer age;
    private Integer gender;
    private String job;
    private Integer wage;

    @Column(columnDefinition = "TEXT")
    private String card;

    private Integer abnormal;

    @Column(name = "abnormal_state", columnDefinition = "JSONB")
    private String abnormalState;

    @Column(name = "user_no")
    private String userNo;

    @Column(name = "loc_id")
    private String locId;
}
