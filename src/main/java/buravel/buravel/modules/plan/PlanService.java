package buravel.buravel.modules.plan;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountRepository;
import buravel.buravel.modules.account.AccountResponseDto;
import buravel.buravel.modules.bookmarkPost.BookmarkPost;
import buravel.buravel.modules.bookmarkPost.BookmarkPostRepository;
import buravel.buravel.modules.planTag.PlanTag;
import buravel.buravel.modules.planTag.PlanTagRepository;
import buravel.buravel.modules.planTag.PlanTagResponseDto;
import buravel.buravel.modules.post.*;
import buravel.buravel.modules.postTag.PostTag;
import buravel.buravel.modules.postTag.PostTagRepository;
import buravel.buravel.modules.postTag.PostTagResponseDto;
import buravel.buravel.modules.tag.Tag;
import buravel.buravel.modules.tag.TagRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Convert;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final PostRepository postRepository;
    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;
    private final PlanTagRepository planTagRepository;
    private final PostTagRepository postTagRepository;
    private final BookmarkPostRepository bookmarkPostRepository;
    private final ModelMapper modelMapper;


    public Plan createPlan(PlanDto planDto, Account account) {
        Account user = accountRepository.findById(account.getId()).get();

        Plan plan = generatePlan(planDto, user);
        PostDto[][] postDtos = planDto.getPostDtos();
        generatePosts(plan,postDtos,user);
        settingOutputPlanTotalPrice(plan);
        settingPlanRating(plan);
        settingTop3ListOfPlan(plan);
        return plan;
    }

    private void settingPlanRating(Plan plan) {
        int count = postRepository.countByPlanOf(plan);
        Float planRating = plan.getPlanRating();
        String val = (planRating / count) + "";
        val = val.substring(0, 3);
        float result = Float.parseFloat(val);
        plan.setPlanRating(result);
    }

    private void settingTop3ListOfPlan(Plan plan) {
        HashMap<Long, String> map = new HashMap<>();
        if (plan.getFlightTotalPrice() > 0) {
            map.put(plan.getFlightTotalPrice(), "FLIGHT");
        }
        if (plan.getDishTotalPrice() > 0) {
            map.put(plan.getDishTotalPrice(), "DISH");
        }
        if (plan.getShoppingTotalPrice() > 0) {
            map.put(plan.getShoppingTotalPrice(), "SHOPPING");
        }
        if (plan.getHotelTotalPrice() > 0) {
            map.put(plan.getHotelTotalPrice(), "HOTEL");
        }
        if (plan.getTrafficTotalPrice() > 0) {
            map.put(plan.getTrafficTotalPrice(), "TRAFFIC");
        }
        if (plan.getEtcTotalPrice() > 0) {
            map.put(plan.getEtcTotalPrice(), "ETC");
        }
        Object[] objects = map.keySet().toArray();
        Arrays.sort(objects);
        for (int i = objects.length - 1; i >= 0; i--) {
            if (plan.getTop3List().size() == 3) {
                break;
            }
            plan.getTop3List().add(map.get(objects[i]));
        }
    }

    private void settingOutputPlanTotalPrice(Plan plan) {
        Long price = plan.getTotalPrice();
        if (price < 10000) {
            plan.setOutputPlanTotalPrice(price + "원");
        }
        String string = price.toString();
        string = string.substring(0, string.length()-4);
        plan.setOutputPlanTotalPrice(string+"만원");
    }


    private void generatePosts(Plan plan, PostDto[][] postDtos, Account user) {
        for (int i = 0; i < postDtos.length; i++) {
            for (int j = 0; j < postDtos[i].length; j++) {
                PostDto postDto = postDtos[i][j];
                Post post = new Post();
                post.setPostTitle(postDto.getPostTitle());
                post.setPrice(postDto.getPrice());
                plan.setTotalPrice(plan.getTotalPrice()+postDto.getPrice());
                post.setOutputPrice(generateOutputPrice(postDto.getPrice()));
                settingPostCategory(plan,post, postDto);
                if (postDto.getPostImage() != null) {
                    post.setPostImage(postDto.getPostImage());
                }else{
                    setPostImageWithCategory(post);
                }

                post.setRating(postDto.getRating());
                plan.setPlanRating(plan.getPlanRating()+ postDto.getRating());
                post.setLat(postDto.getLat());
                post.setLog(postDto.getLog());
                post.setDay(i);
                post.setOrdering(j);
                post.setMemo(postDto.getMemo());
                post.setPostManager(user);
                post.setPlanOf(plan);
                post.setClosed(false);

                if (plan.isPublished() != true) {
                    post.setClosed(true);
                }
                Post saved = postRepository.save(post);

                generatePostTags(saved, postDto.getTags());
            }
        }
    }

    private void setPostImageWithCategory(Post saved) {
        String temp = "";
        PostCategory category = saved.getCategory();
        switch (category.toString()) {
            case "FLIGHT":
                temp = "FLIGHT";
                break;
            case "DISH":
                temp = "DISH";
                break;
            case "SHOPPING":
                temp = "SHOPPING";
                break;
            case "HOTEL":
                temp = "HOTEL";
                break;
            case "TRAFFIC":
                temp = "TRAFFIC";
                break;
            case "ETC":
                temp = "ETC";
                break;
        }
        String uri = imageToDatUri(temp);
        saved.setPostImage(uri);
    }

    private String imageToDatUri(String keyword) {
        byte[] bytes = new byte[0];
        try {
            bytes = getClass().getResourceAsStream("/static/images/"+keyword + ".png").readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String dataUri = new String(Base64.getEncoder().encode(bytes));
        return dataUri;
    }


    private void generatePostTags(Post post, String tags) {
        String[] split = tags.split(",");
        for (String title : split) {
            Tag tag = new Tag();
            tag.setTagTitle(title);
            Tag saved = tagRepository.save(tag);

            PostTag postTag = new PostTag();
            postTag.setPost(post);
            postTag.setTag(tag);
            PostTag save = postTagRepository.save(postTag);

            post.getPostTagList().add(save);
        }
    }

    private void settingPostCategory(Plan plan, Post post, PostDto dto) {
        String category = dto.getCategory();
        switch (category) {
            case "FLIGHT":
                post.setCategory(PostCategory.FLIGHT);
                plan.setFlightTotalPrice(plan.getFlightTotalPrice()+dto.getPrice());
                break;
            case "DISH":
                post.setCategory(PostCategory.DISH);
                plan.setDishTotalPrice(plan.getDishTotalPrice()+dto.getPrice());
                break;
            case "SHOPPING":
                post.setCategory(PostCategory.SHOPPING);
                plan.setShoppingTotalPrice(plan.getShoppingTotalPrice()+dto.getPrice());
                break;
            case "HOTEL":
                post.setCategory(PostCategory.HOTEL);
                plan.setHotelTotalPrice(plan.getHotelTotalPrice()+dto.getPrice());
                break;
            case "TRAFFIC":
                post.setCategory(PostCategory.TRAFFIC);
                plan.setTrafficTotalPrice(plan.getTrafficTotalPrice()+dto.getPrice());
                break;
            case "ETC":
                post.setCategory(PostCategory.ETC);
                plan.setEtcTotalPrice(plan.getEtcTotalPrice()+dto.getPrice());
                break;
        }
    }

    private String generateOutputPrice(Long price) {
        if (price < 10000) {
            return price + "원";
        }
        String string = price.toString();
        string = string.substring(0, string.length()-4);
        return string+"만원";
        // 40000 = 4/0000 = 4만원
    }

    private Plan generatePlan(PlanDto planDto, Account user) {
        Plan plan = new Plan();
        plan.setPlanManager(user);
        plan.setLastModified(LocalDate.now());
        plan.setPlanTitle(planDto.getPlanTitle());
        if (planDto.getPlanImage() != null) {
            plan.setPlanImage(planDto.getPlanImage());
        } else {
            String defaultPlanImage = imageToDatUri("DefaultPlan");
            plan.setPlanImage(defaultPlanImage);
        }
        // image가 null이면 프론트에서 디폴트 이미지
        plan.setPublished(planDto.isPublished());
        plan.setStartDate(planDto.getStartDate());
        plan.setEndDate(planDto.getEndDate());
        //create planTags
        String planTag = planDto.getPlanTag();
        generatePlanTags(plan, planTag);

        return planRepository.save(plan);
    }

    private void generatePlanTags(Plan plan, String planTag) {
        String[] split = planTag.split(",");
        for (String title : split) {
            Tag tag = new Tag();
            tag.setTagTitle(title);
            Tag saved = tagRepository.save(tag);

            PlanTag newPlanTag = new PlanTag();
            newPlanTag.setPlan(plan);
            newPlanTag.setTag(saved);
            PlanTag made = planTagRepository.save(newPlanTag);
            //양방향
            plan.getPlanTagList().add(made);
        }
    }

    public PlanResponseDto createPlanResponse(Account account, Plan plan) {
        PlanResponseDto planResponseDto = modelMapper.map(plan, PlanResponseDto.class);
        AccountResponseDto accountResponseDto = createAccountResponseDto(account);
        planResponseDto.setAccountResponseDto(accountResponseDto);
        List<PlanTag> list = plan.getPlanTagList();
        for (PlanTag planTag : list) {
            PlanTagResponseDto planTagResponseDto = createPlanTagResponseDto(planTag);
            planResponseDto.getPlanTagResponseDtos().add(planTagResponseDto);
        }
        return planResponseDto;
    }

    private AccountResponseDto createAccountResponseDto(Account account) {
        return modelMapper.map(account,AccountResponseDto.class);
    }

    private PlanTagResponseDto createPlanTagResponseDto(PlanTag planTag) {
        PlanTagResponseDto planTagResponseDto = new PlanTagResponseDto();
        planTagResponseDto.setPlanTagTitle(planTag.getTag().getTagTitle());
        return planTagResponseDto;
    }

    public CollectionModel<EntityModel<PlanResponseDto>> findAllPlans() {
        List<Plan> plans = planRepository.findAllByPublishedOrderByLastModified(true);
        List<PlanResponseDto> planResponseDtos = new ArrayList<>();

        for (Plan plan : plans) {
            Long id = plan.getPlanManager().getId();
            Account account = accountRepository.findById(id).get();
            PlanResponseDto planResponse = createPlanResponse(account, plan);
            planResponseDtos.add(planResponse);
        }

        List<EntityModel<PlanResponseDto>> collect =
                planResponseDtos.stream().map(p -> PlanResource.modelOf(p)).collect(Collectors.toList());
        CollectionModel<EntityModel<PlanResponseDto>> result = CollectionModel.of(collect);
        return result;
    }

    public EntityModel<PlanWithPostResponseDto> getPlanWithPlanId(Long id) throws NotFoundException {
        Optional<Plan> byId = planRepository.findById(id);
        if (byId.isEmpty()) {
            throw new NotFoundException("해당 여행계획이 존재하지 않습니다.");
        }
        Plan plan = byId.get();
        List<Post> posts = postRepository.findAllByPlanOf(plan);
        //planWithPostResponseDto의 plan부분 세팅
        PlanWithPostResponseDto ppdto = createPlanWithPostResponseDto(plan);
        //planWithPostResponseDto의 post부분 setting
        for (Post post : posts) {
            PostForPlanResponseDto dto = createPostForPlanResponse(post);
            ppdto.getPostForPlanResponseDtos().add(dto);
        }
        EntityModel<PlanWithPostResponseDto> result = PlanWithPostResource.modelOf(ppdto);
        return result;
        //todo 카테고리 어떻게 응답으로 나오는지 보기
    }

    private PlanWithPostResponseDto createPlanWithPostResponseDto(Plan plan) {
        PlanWithPostResponseDto map = modelMapper.map(plan, PlanWithPostResponseDto.class);
        //planManager부분
        AccountResponseDto aDto = createAccountResponseDto(plan.getPlanManager());
        map.setAccountResponseDto(aDto);
        //planTag부분
        List<PlanTag> planTagList = planTagRepository.findAllByPlan(plan);
        for (PlanTag planTag : planTagList) {
            PlanTagResponseDto ptDto = createPlanTagResponseDto(planTag);
            map.getPlanTagResponseDtos().add(ptDto);
        }
        return map;
    }

    private PostForPlanResponseDto createPostForPlanResponse(Post post) {
        // postTagResponseDto제외 나머지 정보 set
        PostForPlanResponseDto map = modelMapper.map(post, PostForPlanResponseDto.class);
        //현재 포스트의 모든 태그들 -> postTagResponsDto
        List<PostTag> postTagList = postTagRepository.findAllByPost(post);
        for (PostTag postTag : postTagList) {
            PostTagResponseDto postTagResponseDto = new PostTagResponseDto();
            Tag tag = postTag.getTag();
            postTagResponseDto.setPostTagTitle(tag.getTagTitle());
            //PostForPlanResponseDto의 postTagResponseDto리스트에 add
            map.getPostTagResponseDtoList().add(postTagResponseDto);
        }
        return map;
    }

    public Page<Plan> search(String keyword, long min, long max, Pageable pageable) {
        // 금액 검색 x
        if (max == 0) {
            Page<Plan> plan = planRepository.findWithSearchCond(keyword,pageable);
            return plan;
        }
        // 금액 검색 o
        Page<Plan> planWithPrice = planRepository.findWithSearchCondContainsPrice(keyword, min, max, pageable);
        return planWithPrice;
    }

    public Plan updatePlan(PatchPlanRequestDto patchplanRequestDto, Account account) throws NotFoundException {
        Optional<Plan> foundPlanOptional = planRepository.findById(patchplanRequestDto.getPlanId());

        // 여행 계획이 존재하는지 검증
        if (foundPlanOptional.isEmpty()) {
            throw new NotFoundException("여행계획을 찾을 수 없습니다.");
        }
        Plan plan = foundPlanOptional.get();
        Account user = accountRepository.findById(account.getId()).get();

        // 로그인 사용자의 작성글이 맞는지 검증
        if (!user.equals(plan.getPlanManager())) {
            throw new AccessDeniedException("해당 기능을 사용할 권한이 없습니다.");
        }

        //Post, PostTag, Tag 삭제
        deletePostTags(plan);
        postRepository.deleteAllByPlanOf(plan);

        //planTag, Tag 삭제
        deletePlanTags(plan);

        //plan 정보 수정
        updatePlanInfo(patchplanRequestDto, plan, user);

        //post와 각 포스트들 태그 저장 (postTag)
        PostDto[][] postDtos = patchplanRequestDto.getPostDtos();
        generatePosts(plan, postDtos, user);

        // Plan 총 가격/탑3 등 저장
        settingOutputPlanTotalPrice(plan);
        settingTop3ListOfPlan(plan);

        return plan;
    }

    private void deletePlanTags(Plan plan) {
        List<PlanTag> planTagList = plan.getPlanTagList();
        for (PlanTag plantag : planTagList) {
            Tag tag = plantag.getTag();
            tagRepository.delete(tag);
            plantag.setTag(null);
        }
        plan.getPlanTagList().clear();
        planTagRepository.deleteAllByPlan(plan);
    }

    private void deletePostTags(Plan plan) {
        List<Post> beforePostList = postRepository.findAllByPlanOf(plan);
        for (Post beforePost : beforePostList) {
            List<PostTag> postTagList = beforePost.getPostTagList();
            for (PostTag postTag : postTagList) {
                Tag tag = postTag.getTag();
                tagRepository.delete(tag);
                postTag.setTag(null);
            }
            beforePost.setPostTagList(null);

            Optional<BookmarkPost> bookmarkPost = bookmarkPostRepository.findByPost(beforePost);
            if (!bookmarkPost.isEmpty()) {
                BookmarkPost find = bookmarkPost.get();
                beforePost.getBookmarkPosts().remove(find);
                find.setPost(null);
            }
            postTagRepository.deleteAllByPost(beforePost);
        }
    }

    private void updatePlanInfo(PatchPlanRequestDto patchplanRequestDto, Plan plan, Account user) {
        plan.setPlanTitle(patchplanRequestDto.getPlanTitle());
        plan.setLastModified(LocalDate.now());

        // 업로드한 이미지 저장 or 디폴트 이미지 시 디폴트 이미지 저장
        if (patchplanRequestDto.getPlanImage() != null) {
            plan.setPlanImage(patchplanRequestDto.getPlanImage());
        } else {
            String defaultPlanImage = imageToDatUri("DefaultPlan");
            plan.setPlanImage(defaultPlanImage);
        }

        plan.setPublished(patchplanRequestDto.isPublished());
        plan.setStartDate(patchplanRequestDto.getStartDate());
        plan.setEndDate(patchplanRequestDto.getEndDate());

        plan.getTop3List().clear();
        plan.setTotalPrice(0L);
        plan.setFlightTotalPrice(0L);
        plan.setDishTotalPrice(0L);
        plan.setDishTotalPrice(0L);
        plan.setShoppingTotalPrice(0L);
        plan.setHotelTotalPrice(0L);
        plan.setTrafficTotalPrice(0L);
        plan.setEtcTotalPrice(0L);

        // 플랜태그 저장
        String planTag = patchplanRequestDto.getPlanTag();
        generatePlanTags(plan, planTag);
    }

    public PatchPlanResponseDto updatePlanResponse(Plan plan) {
        PatchPlanResponseDto planResponseDto = modelMapper.map(plan, PatchPlanResponseDto.class);

        List<PlanTag> list = plan.getPlanTagList();
        for (PlanTag planTag : list) {
            PlanTagResponseDto planTagResponseDto = createPlanTagResponseDto(planTag);
            planResponseDto.getPlanTagResponseDtos().add(planTagResponseDto);
        }

        List<Post> posts = postRepository.findAllByPlanOf(plan);
        for (Post post : posts) {
            PatchPostReponseDto patchPostReponseDto = modelMapper.map(post, PatchPostReponseDto.class);
            List<PostTag> postTags = post.getPostTagList();
            List<PostTagResponseDto> lists = new ArrayList<>();
            for (PostTag tag : postTags) {
                PostTagResponseDto postTagDto = patchPostTagResponseDto(tag);
                lists.add(postTagDto);
                patchPostReponseDto.getPostTagList().add(postTagDto);
            }
            patchPostReponseDto.setPostTagList(lists);
            planResponseDto.getPostResponseDtos().add(patchPostReponseDto);
        }
        return planResponseDto;
    }

    private PostTagResponseDto patchPostTagResponseDto(PostTag postTag) {
        PostTagResponseDto responseDto = new PostTagResponseDto();
        responseDto.setPostTagTitle(postTag.getTag().getTagTitle());
        return responseDto;
    }

    public void deletePlan(Long planId, Account account) throws NotFoundException {
        // 플랜 존재 검증
        Optional<Plan> postOptional = planRepository.findById(planId);
        if (postOptional.isEmpty()) {
            throw new NotFoundException("해당 여행계획이 존재하지 않습니다.");
        }
        Plan plan = postOptional.get();

        //플랜 작성자가 로그인 유저인지 검증
        Account user = accountRepository.findById(account.getId()).get();
        if (plan.getPlanManager().getId() != user.getId()) {
            throw new NotFoundException("해당 여행계획의 작성자가 아닙니다.");
        }

        //Post, PostTag, Tag 삭제
        deletePostTags(plan);
        postRepository.deleteAllByPlanOf(plan);

        //Plan, planTag, Tag 삭제
        deletePlanTags(plan);
        planRepository.delete(plan);
    }
}
  