package buravel.buravel.modules.mypage;

import buravel.buravel.modules.IndexController;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class MypageResource extends EntityModel<UserInfoResponseDto> {

    public static EntityModel<UserInfoResponseDto> modelOf(UserInfoResponseDto userInfoResponseDto){
        EntityModel<UserInfoResponseDto> userInfoResource = EntityModel.of(userInfoResponseDto);
        userInfoResource.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));

        return userInfoResource;
    }
}
