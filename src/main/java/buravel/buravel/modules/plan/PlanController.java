package buravel.buravel.modules.plan;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.CurrentUser;
import buravel.buravel.modules.errors.ErrorResource;
import buravel.buravel.modules.plan.validator.PatchPlanValidator;
import buravel.buravel.modules.plan.validator.PlanValidator;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final PlanValidator planValidator;
    private final PatchPlanValidator patchPlanValidator;
    private final PlanRepository planRepository;

    @PostMapping
    public ResponseEntity createPlan(@RequestBody @Valid PlanDto planDto, @CurrentUser Account account, Errors errors) throws NotFoundException {
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
        EntityModel<PlanResponseDto> result = planService.addLinksWithCreate(resultResource);
        return ResponseEntity.ok().body(result);

    }

    @GetMapping
    public ResponseEntity getAllPlans() {
        //todo 이걸 그냥 없애고 검색에 pageabledefault로 가져가는게 나을듯
        CollectionModel<EntityModel<PlanResponseDto>> plans = planService.findAllPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/mine/closed")
    public ResponseEntity getMyClosedPlans(@CurrentUser Account account, @PageableDefault(size = 5, sort = "lastModified",
            direction = Sort.Direction.DESC) Pageable pageable, PagedResourcesAssembler<Plan> assembler) {
        Page<Plan> plans =  planService.getMyClosedPlans(account, pageable);
        PagedModel<EntityModel<PlanResponseDto>> entityModels =
                assembler.toModel(plans, p -> PlanResource.modelOf(planService.createPlanResponse(p.getPlanManager(), p)));
        PagedModel<EntityModel<PlanResponseDto>> result = planService.addLinksWithClosedPlans(entityModels);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/mine/published")
    public ResponseEntity getMyPublishedPlans(@CurrentUser Account account, @PageableDefault(size = 5, sort = "lastModified",
            direction = Sort.Direction.DESC) Pageable pageable, PagedResourcesAssembler<Plan> assembler) {
        Page<Plan> plans =  planService.getMyPublishedPlans(account, pageable);
        PagedModel<EntityModel<PlanResponseDto>> entityModels =
                assembler.toModel(plans, p -> PlanResource.modelOf(planService.createPlanResponse(p.getPlanManager(), p)));
        PagedModel<EntityModel<PlanResponseDto>> result = planService.addLinksWithPublishedPlans(entityModels);
        return ResponseEntity.ok(entityModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity getPlan(@PathVariable Long id) throws NotFoundException {
        if (!planRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<PlanWithPostResponseDto> model = planService.getPlanWithPlanId(id);
        EntityModel<PlanWithPostResponseDto> result = planService.addLinksWithGetPlan(model);
        return ResponseEntity.ok(result);
    }

    @PatchMapping
    public ResponseEntity updatePlan(@RequestBody @Valid PatchPlanRequestDto patchplanRequestDto, @CurrentUser Account account, Errors errors) throws NotFoundException  {
        // request 에러 체크
        if (errors.hasErrors()) {
            EntityModel<Errors> jsr303 = ErrorResource.of(errors);
            return ResponseEntity.badRequest().body(jsr303);
        }
        // 여행 날짜 request 체크
        patchPlanValidator.validate(patchplanRequestDto,errors);
        if (errors.hasErrors()) {
            EntityModel<Errors> customError = ErrorResource.of(errors);
            return ResponseEntity.badRequest().body(customError);
        }

        // 플랜 업데이트
        Plan plan = planService.updatePlan(patchplanRequestDto, account);

        PatchPlanResponseDto planResponseDto = planService.updatePlanResponse(plan);
        EntityModel<PatchPlanResponseDto> resultResource = EntityModel.of(planResponseDto);
        resultResource.add(linkTo(PlanController.class).withSelfRel());
        return ResponseEntity.ok().body(resultResource);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity deletePlan(@PathVariable Long planId, @CurrentUser Account account) throws NotFoundException {
        if (!planRepository.existsById(planId)) {
            return ResponseEntity.notFound().build();
        }
        planService.deletePlan(planId, account);
        return ResponseEntity.ok().build();
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
