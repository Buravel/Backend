package buravel.buravel.modules.plan;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.planTag.PlanTag;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@NamedEntityGraph(
        name = "Plan.withAll",
        attributeNodes ={
            @NamedAttributeNode("planManager"),
            @NamedAttributeNode(value = "planTagList",subgraph = "tags")
        },
        subgraphs =@NamedSubgraph(name = "tags",attributeNodes =@NamedAttributeNode("tag") )
)
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
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

    private Float planRating=0F;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastModified;

    private Long totalPrice= 0L;
    private String outputPlanTotalPrice; // post랑 비슷하게 만원단위로 끊어서 string으로 리턴

    private Long flightTotalPrice = 0L;
    private Long dishTotalPrice= 0L;
    private Long shoppingTotalPrice= 0L;
    private Long hotelTotalPrice= 0L;
    private Long trafficTotalPrice= 0L;
    private Long etcTotalPrice= 0L;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> top3List = new HashSet<>();
    //spring data jpa 사용 시 set처럼 순서가 없고 list처럼 중복을 허용하는 경우 MultipleBagFetchException발생
    // 하이버네이트는 list를 bag으로 사용 -> 우리의 서비스에서 top3list는 말 그대로 상위 3개를 표현하기 위한 것
    // 상위 3개의 순서는 중요하지 않다. set으로 변경하여 해결
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