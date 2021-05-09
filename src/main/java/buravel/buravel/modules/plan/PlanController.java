package buravel.buravel.modules.plan;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.CurrentUser;
import buravel.buravel.modules.errors.ErrorResource;
import buravel.buravel.modules.plan.validator.PlanValidator;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final PlanValidator planValidator;


    @PostMapping
    public ResponseEntity createPlan(@RequestBody @Valid PlanDto planDto, @CurrentUser Account account, Errors errors) {
        if (errors.hasErrors()) {
            EntityModel<Errors> jsr303 = ErrorResource.of(errors);
            return ResponseEntity.badRequest().body(jsr303);
        }
        planValidator.validate(planDto,errors);
        if (errors.hasErrors()) {
            EntityModel<Errors> customError = ErrorResource.of(errors);
            return ResponseEntity.badRequest().body(customError);
        }
        Plan plan = planService.createPlan(planDto, account);
        PlanResponseDto planResponseDto = planService.createPlanResponse(account, plan);
        EntityModel<PlanResponseDto> resultResource = PlanResource.modelOf(planResponseDto);
        return ResponseEntity.ok().body(resultResource);

    }

    @GetMapping
    public ResponseEntity getAllPlans() {
        //todo 이걸 그냥 없애고 검색에 pageabledefault로 가져가는게 나을듯
        CollectionModel<EntityModel<PlanResponseDto>> plans = planService.findAllPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    public ResponseEntity getPlan(@PathVariable Long id) throws NotFoundException {
        EntityModel<PlanWithPostResponseDto> result = planService.getPlanWithPlanId(id);
        return ResponseEntity.ok(result);
    }
}

  /*
  * {
     "planTitle":"blue",
     "published":"true",
     "startDate":"2021-05-02",
     "endDate":"2021-05-03",
     "planTag":"swiss,itlay",
     "postDtos":
     [
         [
          {"postTitle":"first","price":"10000","postImage":"image","category":"FLIGHT","rating":"4.5","lat":"12.12","log":"21.21","tags":"spring,hello"},
          {"postTitle":"secocnd","price":"20000","category":"DISH","rating":"4.5","lat":"12.12","log":"21.21","tags":"spring,hello"}
         ],
         [
          {"postTitle":"third","price":"30000","postImage":"222","category":"ETC","rating":"4.5","lat":"12.12","log":"21.21","tags":"spring,hello"},
          {"postTitle":"forth","price":"40000","category":"HOTEL","rating":"4.5","lat":"12.12","log":"21.21","tags":"spring,hello"}
         ],
         [
          {"postTitle":"fifth","price":"10000","category":"SHOPPING","rating":"4.5","lat":"12.12","log":"21.21","tags":"spring,hello"},
          {"postTitle":"sixth","price":"20000","category":"TRAFFIC","rating":"4.5","lat":"12.12","log":"21.21","tags":"spring,hello"}
         ]
     ]
  }
  * */
