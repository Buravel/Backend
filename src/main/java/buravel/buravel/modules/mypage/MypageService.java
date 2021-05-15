package buravel.buravel.modules.mypage;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;

@Service
@Transactional
@RequiredArgsConstructor
public class MypageService {

    private final AccountRepository accountRepository;
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

    public UserInfoResponseDto getUserInfo(Account account) {
        Account user = accountRepository.findById(account.getId()).get();
        return generateUserInfoResponseDto(user);
    }

    public UserInfoResponseDto updateUser(Account account, UserRequestDto userRequestDto) {
        Account user = accountRepository.findById(account.getId()).get();
        // 닉네임 변경
        if (!userRequestDto.getNickname().equals(user.getNickname())) {
            user.setNickname(userRequestDto.getNickname());
        }
        // 비밀번호 변경
        if (!userRequestDto.getPassword().equals("")) {
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }

        return generateUserInfoResponseDto(user);
    }
}
