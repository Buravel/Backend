package buravel.buravel.modules.plan;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.CurrentUser;
import buravel.buravel.modules.errors.ErrorResource;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public ResponseEntity createPlan(@RequestBody @Valid PlanDto planDto, @CurrentUser Account account, Errors errors) {
        if (errors.hasErrors()) {
            EntityModel<Errors> jsr303 = ErrorResource.of(errors);
            return ResponseEntity.badRequest().body(jsr303);
        }
        Plan plan = planService.createPlan(planDto, account);
        PlanResponseDto planResponseDto = planService.createPlanResponse(account, plan);
        EntityModel<PlanResponseDto> resultResource = PlanResource.modelOf(planResponseDto);
        return ResponseEntity.ok().body(resultResource);

    }
}

  /*{
     "planTitle":"buravel",
     "published":"false",
     "startDate":"2021-05-02",
     "endDate":"2021-05-02",
     "planTag":"swiss,itlay",
     "postDtos":
     [
         [
          {"postTitle":"first","price":"10000","category":"FLIGHT","rating":"4.5","lat":"12.12","log":"21.21","tags":"spring,hello"},
          {"postTitle":"secocnd","price":"20000","category":"DISH","rating":"4.5","lat":"12.12","log":"21.21","tags":"spring,hello"}
         ],
         [
          {"postTitle":"third","price":"30000","category":"FLIGHT","rating":"4.5","lat":"12.12","log":"21.21","tags":"spring,hello"},
          {"postTitle":"forth","price":"40000","category":"DISH","rating":"4.5","lat":"12.12","log":"21.21","tags":"spring,hello"}
         ]
     ]
  }*/
