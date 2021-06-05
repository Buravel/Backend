package buravel.buravel.modules.plan.dtos;

import buravel.buravel.modules.account.dtos.AccountResponseDto;
import buravel.buravel.modules.planTag.PlanTagResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Lob;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponseDto {
    private Long id;
    private String planTitle;
    @Lob
    private String planImage;
    private boolean published;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private Float planRating;
    private Long totalPrice;
    private String outputPlanTotalPrice;
    private Long flightTotalPrice;
    private Long dishTotalPrice;
    private Long shoppingTotalPrice;
    private Long hotelTotalPrice;
    private Long trafficTotalPrice;
    private Long etcTotalPrice;
    private List<String> top3List = new ArrayList<>();
    // account
    private AccountResponseDto accountResponseDto;
    //planTag
    private List<PlanTagResponseDto> planTagResponseDtos = new ArrayList<>();
}
