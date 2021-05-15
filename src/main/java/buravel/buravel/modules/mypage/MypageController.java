package buravel.buravel.modules.mypage;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.CurrentUser;
import buravel.buravel.modules.errors.ErrorResource;
import buravel.buravel.modules.mypage.validator.UpdateUserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;
    private final UpdateUserValidator validator;

    @PatchMapping("/mypage/picture")
    public ResponseEntity updateUserPicture(@RequestBody PictureRequestDto pictureRequestDto, @CurrentUser Account account){
        UserInfoResponseDto userInfo = mypageService.updateUserPicture(pictureRequestDto, account);

        EntityModel<UserInfoResponseDto> userInfoResource = MypageResource.modelOf(userInfo);
        return ResponseEntity.ok(userInfo);
    }

    @DeleteMapping("/account")
    public ResponseEntity deleteAccount(@CurrentUser Account account){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mypage")
    public ResponseEntity getUserinfo(@CurrentUser Account account) {
        UserInfoResponseDto userInfo = mypageService.getUserInfo(account);
        return ResponseEntity.ok(userInfo);
    }

    @PatchMapping("/mypage")
    public ResponseEntity updateUserinfo(@RequestBody @Valid  UserRequestDto userRequestDto, @CurrentUser Account account, Errors errors) {
        // 패스워드 입력 시 패턴 확인
        if (!userRequestDto.getPassword().equals("")) {
            validator.isRegexPassword(userRequestDto.getPassword(), errors);
        }

        // 닉네임 변경 시 중복 확인
        if (!account.getNickname().equals(userRequestDto.getNickname())) {
            validator.validate(userRequestDto, errors);
        }
        if(errors.hasErrors()){
            EntityModel<Errors> error1 = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error1);
        }

        // 수정
        UserInfoResponseDto userInfo = mypageService.updateUser(account, userRequestDto);
        return ResponseEntity.ok(userInfo);
    }
}
