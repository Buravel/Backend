package buravel.buravel.modules.plan;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.planTag.PlanTag;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    @Id
    @GeneratedValue
    @Column(name = "plan_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account planManager;

    @Column(nullable = false)
    private String planTitle;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String planImage;

    private boolean published = false;

    private LocalDateTime lastModified;

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Long totalPrice;
    private String outputPlanTotalPrice; // post랑 비슷하게 만원단위로 끊어서 string으로 리턴

    private Long flightTotalPrice;
    private Long dishTotalPrice;
    private Long shoppingTotalPrice;
    private Long hotelTotalPrice;
    private Long trafficTotalPrice;
    private Long etcTotalPrice;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> top3List = new ArrayList<>();
    /**
     * 카테고리별 토탈프라이스 hashMap써서 정렬하고
     * flightTotalPrice : 123456
     * dishTotalPrice : 12345
     * hotelTotalPrice : 1234 순으로 나오면
     * top3List에 flightTotalPrice - 123456(string으로) -dishTotalPrice - 12345 ..
     * 순서대로 insert하고 프론트에선 그냥 앞에서부터 하나씩 꺼내쓰면될듯
     * <p>
     * 만약 중간에 뭐가 추가된다면 sorting다시할거고 그럼 리스트  removeAll하고 다시 처음부터 insert해주면 된다.
     */

    @OneToMany(mappedBy = "plan")
    private List<PlanTag> planTagList = new ArrayList<>();
}