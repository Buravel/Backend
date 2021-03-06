package buravel.buravel.modules.mypage;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountRepository;
import buravel.buravel.modules.bookmark.Bookmark;
import buravel.buravel.modules.bookmark.BookmarkRepository;
import buravel.buravel.modules.bookmark.BookmarkService;
import buravel.buravel.modules.mypage.dtos.PictureRequestDto;
import buravel.buravel.modules.mypage.dtos.UserInfoResponseDto;
import buravel.buravel.modules.mypage.dtos.UserNicknameRequestDto;
import buravel.buravel.modules.mypage.dtos.UserPasswordRequestDto;
import buravel.buravel.modules.plan.Plan;
import buravel.buravel.modules.plan.PlanRepository;
import buravel.buravel.modules.plan.PlanService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@Transactional
@RequiredArgsConstructor
public class MypageService {

    private final AccountRepository accountRepository;
    private final PlanRepository planRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PlanService planService;
    private final BookmarkService bookmarkService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserInfoResponseDto updateUserPicture(PictureRequestDto pictureRequestDto, Account account){
        String image = pictureRequestDto.getProfileImage();
        Account user = accountRepository.findById(account.getId()).get();

        if(image != null){
            user.setProfileImage(image);
        }
        else{
            user.setProfileImage(getDefaultImage());
        }

        return generateUserInfoResponseDto(user);
    }

    public String getDefaultImage(){
        byte[] bytes = new byte[0];

        try {
            // ?????? ????????? ???????????? ????????? ?????? ?????? ????????????. ?????? ?????? ?????? ???????????? ???????????? ????????? ??? ??????
            bytes = getClass().getResourceAsStream("/static/images/DefaultPlan.png").readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(Base64.getEncoder().encode(bytes));
    }

    public UserInfoResponseDto generateUserInfoResponseDto(Account account){
        UserInfoResponseDto user = modelMapper.map(account, UserInfoResponseDto.class);

        return user;
    }

    public void deleteAccount(Account account) throws NotFoundException {
        // ????????? ?????? ????????? ??? ???????????? ????????? ???????????? ????????? ??? ??????.
        // ?????? ???????????? ?????? ??????, ????????? ????????? ?????? ??? ????????? ????????? ????????? ??????.
        // ????????? ????????? ?????? delete???. ?????? ????????? ??????????????? ?????? ??????
        Account user = accountRepository.findById(account.getId()).get();

        List<Plan> planList = planRepository.findAllByPlanManager(user);
        for(Plan plan : planList){
            planService.deletePlan(plan.getId(), user);
        } // ????????? ?????? plan ??????. todo: ???????????? ??? ????????? ?????????.

        List<Bookmark> bookmarkList = bookmarkRepository.findAllByBookmarkManager(user);
        for(Bookmark bookmark : bookmarkList){
            bookmarkService.deleteBookmark(bookmark.getId(), user);
        } // ?????? ?????? bookmark ??????.

        accountRepository.deleteById(user.getId()); // ?????? ?????? ??????
    }

    public UserInfoResponseDto getUserInfo(Account account) {
        Account user = accountRepository.findById(account.getId()).get();
        return generateUserInfoResponseDto(user);
    }

    public UserInfoResponseDto updateUserNickname(Account account, UserNicknameRequestDto userNicknameRequestDto) {
        Account user = accountRepository.findById(account.getId()).get();
        user.setNickname(userNicknameRequestDto.getNickname());

        return generateUserInfoResponseDto(user);
    }

    public UserInfoResponseDto updateUserPassword(Account account, UserPasswordRequestDto userPasswordRequestDto) {
        Account user = accountRepository.findById(account.getId()).get();
        user.setPassword(passwordEncoder.encode(userPasswordRequestDto.getPassword()));

        return generateUserInfoResponseDto(user);
    }

    public UserInfoResponseDto checkPassword(Account account, UserPasswordRequestDto userPasswordRequestDto){
        Account user = accountRepository.findById(account.getId()).get();

        if(!passwordEncoder.matches(userPasswordRequestDto.getPassword(), user.getPassword()))
            return null;

        return generateUserInfoResponseDto(user);
    }

    public EntityModel<UserInfoResponseDto> addLinksChangePicture(UserInfoResponseDto userInfo){
        EntityModel<UserInfoResponseDto> userResource = MypageResource.modelOf(userInfo);

        userResource.add(linkTo(MypageController.class).withRel("myInfo"));
        userResource.add(linkTo(MypageController.class).slash("nickname").withRel("changeNickname"));
        userResource.add(linkTo(MypageController.class).slash("password").withRel("changePassword"));

        return userResource;
    }

    public EntityModel<UserInfoResponseDto> addLinksChangeNickname(UserInfoResponseDto userInfo){
        EntityModel<UserInfoResponseDto> userResource = MypageResource.modelOf(userInfo);

        userResource.add(linkTo(MypageController.class).withRel("myInfo"));
        userResource.add(linkTo(MypageController.class).slash("picture").withRel("changePicture"));
        userResource.add(linkTo(MypageController.class).slash("password").withRel("changePassword"));

        return userResource;
    }

    public EntityModel<UserInfoResponseDto> addLinksChangePassword(UserInfoResponseDto userInfo){
        EntityModel<UserInfoResponseDto> userResource = MypageResource.modelOf(userInfo);

        userResource.add(linkTo(MypageController.class).withRel("myInfo"));
        userResource.add(linkTo(MypageController.class).slash("picture").withRel("changePicture"));
        userResource.add(linkTo(MypageController.class).slash("nickname").withRel("changeNickname"));

        return userResource;
    }

    public EntityModel<UserInfoResponseDto> addLinksGetUser(UserInfoResponseDto userInfo){
        EntityModel<UserInfoResponseDto> userResource = MypageResource.modelOf(userInfo);

        userResource.add(linkTo(MypageController.class).slash("picture").withRel("changePicture"));
        userResource.add(linkTo(MypageController.class).slash("nickname").withRel("changeNickname"));
        userResource.add(linkTo(MypageController.class).slash("password").withRel("changePassword"));

        return userResource;
    }

    public EntityModel<UserInfoResponseDto> addLinksCheckPassword(UserInfoResponseDto userInfo){
        EntityModel<UserInfoResponseDto> userResource = EntityModel.of(userInfo);

        userResource.add(linkTo(MypageController.class).withRel("myInfo"));
        return userResource;
    }
}
