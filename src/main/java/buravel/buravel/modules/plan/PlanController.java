package buravel.buravel.modules.plan;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountRepository;
import buravel.buravel.modules.account.CurrentUser;
import buravel.buravel.modules.errors.ErrorResource;
import buravel.buravel.modules.plan.dtos.*;
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
    private final AccountRepository accountRepository;

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
        EntityModel<PlanResponseDto> result = planService.addLinksWithCreate(resultResource);
        return ResponseEntity.ok().body(result);

    }

    @GetMapping
    public ResponseEntity getAllPlans() {
        //todo ?????? ?????? ????????? ????????? pageabledefault??? ??????????????? ?????????
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
    public ResponseEntity getPlan(@PathVariable Long id)  {
        if (!planRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<PlanWithPostResponseDto> model = null;
        try {
            model = planService.getPlanWithPlanId(id);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<PlanWithPostResponseDto> result = planService.addLinksWithGetPlan(model);
        return ResponseEntity.ok(result);
    }

    @PatchMapping
    public ResponseEntity updatePlan(@RequestBody @Valid PatchPlanRequestDto patchplanRequestDto, @CurrentUser Account account, Errors errors) throws NotFoundException  {
        // request ?????? ??????
        if (errors.hasErrors()) {
            EntityModel<Errors> jsr303 = ErrorResource.of(errors);
            return ResponseEntity.badRequest().body(jsr303);
        }
        // ?????? ?????? request ??????
        patchPlanValidator.validate(patchplanRequestDto,errors);
        if (errors.hasErrors()) {
            EntityModel<Errors> customError = ErrorResource.of(errors);
            return ResponseEntity.badRequest().body(customError);
        }

        // ?????? ????????????
        Plan plan = planService.updatePlan(patchplanRequestDto, account);

        PatchPlanResponseDto planResponseDto = planService.updatePlanResponse(plan);
        EntityModel<PatchPlanResponseDto> model = PatchPlanResource.modelOf(planResponseDto);
        EntityModel<PatchPlanResponseDto> result = planService.addLinksPatchPlan(model);
        return ResponseEntity.ok().body(result);
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


