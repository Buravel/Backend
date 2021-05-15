package buravel.buravel.modules.mypage;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MypageService {

    private final AccountRepository accountRepository;
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

    public void deleteAccount(Account account){
        // 유저의 모든 기록을 다 지우는건 데이터 측면에서 별로일 것 같음.
        // 그래서 차라리 account 내용 다 지우고 id랑 verified만 남기는 방향으로.
        // 대신 유저 탈퇴 시, 게시글은 계속 남는다 라는 경고창을 띄우는 쪽으로.
        // 문제는 그럼, 비공개한 게시글과 북마크도 남겨야 하는가? -> 이걸 지우기 위해선 양방향 필요할 듯..
        Account user = accountRepository.findById(account.getId()).get();
    }
}
