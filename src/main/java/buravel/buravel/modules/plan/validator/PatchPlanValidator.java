package buravel.buravel.modules.plan.validator;

import buravel.buravel.modules.plan.dtos.PatchPlanRequestDto;
import buravel.buravel.modules.plan.dtos.PlanDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class PatchPlanValidator implements Validator  {

    @Override
    public boolean supports(Class<?> aClass) {
    return aClass.isAssignableFrom(PlanDto.class);
}

    @Override
    public void validate(Object target, Errors errors) {
        PatchPlanRequestDto tar = (PatchPlanRequestDto) target;
        if (tar.getEndDate().isBefore(tar.getStartDate())) {
            errors.rejectValue("endDate", "invalid endDate", new Object[]{tar.getEndDate()}, "여행 종료 시간을 확인해주세요.");
        }
    }
}
