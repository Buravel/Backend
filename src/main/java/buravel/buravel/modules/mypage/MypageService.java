package buravel.buravel.modules.mypage;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountRepository;
import buravel.buravel.modules.bookmark.Bookmark;
import buravel.buravel.modules.bookmark.BookmarkRepository;
import buravel.buravel.modules.bookmark.BookmarkService;
import buravel.buravel.modules.plan.Plan;
import buravel.buravel.modules.plan.PlanRepository;
import buravel.buravel.modules.plan.PlanService;
import buravel.buravel.modules.post.PostRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

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
            // 유저 디폴트 이미지가 없어서 일단 플랜 디폴트로. 근데 그냥 플랜 디폴트로 통일해도 괜찮을 것 같음
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
        // 유저의 모든 기록을 다 지우는건 데이터 측면에서 별로일 것 같음.
        // 근데 애매하게 남길 경우, 나중에 문제가 생길 수 있을거 같다는 의견이 존재.
        // 그래서 우선은 전부 delete로. 이후 수정이 필요하다면 그때 변경
        Account user = accountRepository.findById(account.getId()).get();

        List<Bookmark> bookmarkList = bookmarkRepository.findAllByBookmarkManager(user);
        for(Bookmark bookmark : bookmarkList){
            bookmarkService.deleteBookmark(bookmark.getId(), user);
        } // 만든 모든 bookmark 삭제.

        List<Plan> planList = planRepository.findAllByPlanManager(user);
        for(Plan plan : planList){
            planService.deletePlan(plan.getId(), user);
        } // 작성한 모든 plan 삭제. todo: 남긴다면 이 부분이 수정됨.

        accountRepository.deleteById(user.getId()); // 유저 정보 삭제
    }
}
