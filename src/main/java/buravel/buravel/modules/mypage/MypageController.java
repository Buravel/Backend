package buravel.buravel.modules.mypage;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.CurrentUser;
import buravel.buravel.modules.errors.ErrorResource;
import buravel.buravel.modules.mypage.validator.UpdateUserNicknameValidator;
import javassist.NotFoundException;
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
    private final UpdateUserNicknameValidator validator;

    @PatchMapping("/mypage/picture")
    public ResponseEntity updateUserPicture(@RequestBody PictureRequestDto pictureRequestDto, @CurrentUser Account account){
        UserInfoResponseDto userInfo = mypageService.updateUserPicture(pictureRequestDto, account);

        EntityModel<UserInfoResponseDto> userInfoResource = MypageResource.modelOf(userInfo);
        return ResponseEntity.ok(userInfoResource);
    }

    @DeleteMapping("/account")
    public ResponseEntity deleteAccount(@CurrentUser Account account) throws NotFoundException {
        mypageService.deleteAccount(account);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/mypage")
    public ResponseEntity getUserinfo(@CurrentUser Account account) {
        UserInfoResponseDto userInfo = mypageService.getUserInfo(account);
        return ResponseEntity.ok(userInfo);
    }

    @PatchMapping("/mypage/nickname")
    public ResponseEntity updateUserNickname(@RequestBody @Valid UserNicknameRequestDto userNicknameRequestDto, @CurrentUser Account account, Errors errors) {
        if(errors.hasErrors()){
            EntityModel<Errors> error1 = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error1);
        }
        validator.validate(userNicknameRequestDto, account, errors);
        if(errors.hasErrors()){
            EntityModel<Errors> error2 = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error2);
        }

        UserInfoResponseDto userInfo = mypageService.updateUserNickname(account, userNicknameRequestDto);
        return ResponseEntity.ok(userInfo);
    }

    @PatchMapping("/mypage/password")
    public ResponseEntity updateUserPassword(@RequestBody @Valid UserPasswordRequestDto userPasswordRequestDto, @CurrentUser Account account, Errors errors) {
        if(errors.hasErrors()){
            EntityModel<Errors> error1 = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error1);
        }
        UserInfoResponseDto userInfo = mypageService.updateUserPassword(account, userPasswordRequestDto);
        return ResponseEntity.ok(userInfo);
    }

}
