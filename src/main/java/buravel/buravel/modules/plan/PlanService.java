package buravel.buravel.modules.plan;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountRepository;
import buravel.buravel.modules.account.AccountResponseDto;
import buravel.buravel.modules.planTag.PlanTag;
import buravel.buravel.modules.planTag.PlanTagRepository;
import buravel.buravel.modules.planTag.PlanTagResponseDto;
import buravel.buravel.modules.post.Post;
import buravel.buravel.modules.post.PostCategory;
import buravel.buravel.modules.post.PostDto;
import buravel.buravel.modules.post.PostRepository;
import buravel.buravel.modules.postTag.PostTag;
import buravel.buravel.modules.postTag.PostTagRepository;
import buravel.buravel.modules.tag.Tag;
import buravel.buravel.modules.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private final ModelMapper modelMapper;


    public Plan createPlan(PlanDto planDto, Account account) {
        Account user = accountRepository.findById(account.getId()).get();

        Plan plan = generatePlan(planDto, user);
        PostDto[][] postDtos = planDto.getPostDtos();
        generatePosts(plan,postDtos,user);
        settingOutputPlanTotalPrice(plan);
        settingTop3ListOfPlan(plan);
        return plan;
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
                if (postDto.getPostImage() != null) {
                    post.setPostImage(postDto.getPostImage());
                }else{
                    setPostImageWithCategory(post);
                }

                settingPostCategory(plan,post, postDto);
                post.setRating(postDto.getRating());
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
        //todo 현재 디폴트이미지 아직 못 받음
        if (saved.getCategory() == PostCategory.FLIGHT) {
            saved.setPostImage("default FLIGHT");
        } else if (saved.getCategory() == PostCategory.DISH) {
            saved.setPostImage("default DISH");
        } else if (saved.getCategory() == PostCategory.SHOPPING) {
            saved.setPostImage("default SHOPPING");
        } else if (saved.getCategory() == PostCategory.HOTEL) {
            saved.setPostImage("default HOTEL");
        } else if (saved.getCategory() == PostCategory.TRAFFIC) {
            saved.setPostImage("default TRAFFIC");
        } else {
            saved.setPostImage("default ETC");
        }
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
        plan.setPlanTitle(planDto.getPlanTitle());
        if (planDto.getPlanImage() != null) {
            plan.setPlanImage(planDto.getPlanImage());
        }
        //todo 그게아니면 default이미지로 설정
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

}

  