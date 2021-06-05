package buravel.buravel.modules;

import buravel.buravel.modules.plan.Plan;
import buravel.buravel.modules.plan.PlanResource;
import buravel.buravel.modules.plan.dtos.PlanResponseDto;
import buravel.buravel.modules.plan.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
@RequiredArgsConstructor
public class IndexController {

    private final PlanService planService;

    @GetMapping("/")
    public ResponseEntity index() {
        return null;
    }

    @GetMapping("/search")
    public ResponseEntity search(@RequestParam("keyword") String keyword, @RequestParam("min") long min,
                                 @RequestParam("max") long max, @PageableDefault(size = 10,sort = "lastModified",
            direction = Sort.Direction.DESC) Pageable pageable,
                                 PagedResourcesAssembler<Plan> assembler) {
        //검색의 경우는 잘못된 값을 집어넣으면 그냥 그거에 따라 결과가안나옴
        //굳이 에러처리 필요 x
        //sort는 직접 넣어보기 sort=~ & dir=desc
        Page<Plan> plan = planService.search(keyword, min, max, pageable);
        PagedModel<EntityModel<PlanResponseDto>> model =
                assembler.toModel(plan, p -> PlanResource.modelOf(planService.createPlanResponse(p.getPlanManager(), p)));
        PagedModel<EntityModel<PlanResponseDto>> result = planService.addLinksWithSearch(model);
        return ResponseEntity.ok(result);
    }
}
