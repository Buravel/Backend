package buravel.buravel.modules.plan;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    /**
     * Plan 작성 API
     */
    @PostMapping
    public ResponseEntity createPlan(@RequestBody PlanDto planDto, @CurrentUser Account account) {
        Plan plan = planService.createPlan(planDto, account);
        PlanResponseDto planResponseDto = planService.createPlanResponse(account,plan);
        EntityModel<PlanResponseDto> resultResource = PlanResource.modelOf(planResponseDto);
        return ResponseEntity.ok().body(resultResource);
    }

    /**
     * Plan 수정 API
     */
    @PatchMapping("")
    public ResponseEntity updatePlan(@RequestBody PatchPlanRequestDto patchplanRequestDto) {
        Plan plan = planService.updatePlan(patchplanRequestDto);
        PatchPlanResponseDto planResponseDto = planService.updatePlanResponse(plan);
        EntityModel<PatchPlanResponseDto> resultResource = EntityModel.of(planResponseDto);
        resultResource.add(linkTo(PlanController.class).withSelfRel());

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
