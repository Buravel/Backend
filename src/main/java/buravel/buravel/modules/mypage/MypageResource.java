package buravel.buravel.modules.mypage;

import buravel.buravel.modules.IndexController;
import buravel.buravel.modules.plan.PlanController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class MypageResource extends EntityModel<UserInfoResponseDto> {

    public static EntityModel<UserInfoResponseDto> modelOf(UserInfoResponseDto userInfoResponseDto){
        EntityModel<UserInfoResponseDto> userInfoResource = EntityModel.of(userInfoResponseDto);

        userInfoResource.add(linkTo(IndexController.class).slash("search").withRel("search"));
        userInfoResource.add(linkTo(PlanController.class).slash("mine").slash("closed").withRel("getMyClosedPlans"));
        userInfoResource.add(linkTo(PlanController.class).slash("mine").slash("published").withRel("getMyPublishedPlans"));
        userInfoResource.add(linkTo(MypageController.class).slash("account").withRel("deleteAccount"));
        userInfoResource.add(Link.of("https://docs.google.com/spreadsheets/d/1XmJZD9VyPquyhDm9XyfJL_KO89xLlN5-VTQGh6MTW-g/edit#gid=0").withRel("profile"));

        return userInfoResource;
    }
}
