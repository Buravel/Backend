package buravel.buravel.modules.account;

import buravel.buravel.modules.plan.Plan;
import buravel.buravel.modules.plan.PlanResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountWithPlanDto {
    private Account account;
    private PlanResponseDto planResponseDto;
}
