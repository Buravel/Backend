package buravel.buravel.modules.account.dtos;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.plan.dtos.PlanResponseDto;
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
