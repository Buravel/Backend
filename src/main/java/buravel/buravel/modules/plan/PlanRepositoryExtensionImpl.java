package buravel.buravel.modules.plan;

import buravel.buravel.modules.account.QAccount;
import buravel.buravel.modules.planTag.QPlanTag;
import buravel.buravel.modules.tag.QTag;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static buravel.buravel.modules.account.QAccount.account;
import static buravel.buravel.modules.plan.QPlan.plan;
import static buravel.buravel.modules.planTag.QPlanTag.planTag;
import static buravel.buravel.modules.tag.QTag.tag;

public class PlanRepositoryExtensionImpl extends QuerydslRepositorySupport implements PlanRepositoryExtension {

    public PlanRepositoryExtensionImpl() {
        super(Plan.class);
    }

    // 연관관계에 있어서는 쿼리 , 카운트 쿼리 2방으로 최적화
    // elementCollection까지 한 방에 가져오는 방법은 마땅히 없는듯
    @Override
    public Page<Plan> findWithSearchCond(String keyword, Pageable pageable) {
        // 공개된 plan 중 제목 / 태그에 해당 키워드가 존재하는 plan 끌어오기
        JPQLQuery<Plan> query = from(plan)
                .where(plan.published.isTrue()
                        .and(plan.planTitle.containsIgnoreCase(keyword)
                                .or(plan.planTagList.any().tag.tagTitle.containsIgnoreCase(keyword))))
                .leftJoin(plan.planManager, account).fetchJoin()
                .leftJoin(plan.planTagList, planTag).fetchJoin()
                .leftJoin(planTag.tag, tag).fetchJoin()
                .distinct();
        JPQLQuery<Plan> planQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Plan> result = planQuery.fetchResults();
        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    @Override
    public Page<Plan> findWithSearchCondContainsPrice(String keyword, long min, long max, Pageable pageable) {
        JPQLQuery<Plan> queryWithPrice = from(plan)
                .where(plan.published.isTrue()
                        .and(plan.totalPrice.between(min,max))
                        .and(plan.planTitle.containsIgnoreCase(keyword)
                                .or(plan.planTagList.any().tag.tagTitle.containsIgnoreCase(keyword))))
                .leftJoin(plan.planManager, account).fetchJoin()
                .leftJoin(plan.planTagList, planTag).fetchJoin()
                .leftJoin(planTag.tag, tag).fetchJoin()
                .distinct();
        JPQLQuery<Plan> planQuery = getQuerydsl().applyPagination(pageable, queryWithPrice);
        QueryResults<Plan> result = planQuery.fetchResults();
        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }
}
