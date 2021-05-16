package buravel.buravel.modules.mypage;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.CurrentUser;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    @PatchMapping("/mypage/picture")
    public ResponseEntity updateUserPicture(@RequestBody PictureRequestDto pictureRequestDto, @CurrentUser Account account){
        UserInfoResponseDto userInfo = mypageService.updateUserPicture(pictureRequestDto, account);

        EntityModel<UserInfoResponseDto> userInfoResource = MypageResource.modelOf(userInfo);
        return ResponseEntity.ok(userInfo);
    }

    @DeleteMapping("/account")
    public ResponseEntity deleteAccount(@CurrentUser Account account) throws NotFoundException {
        mypageService.deleteAccount(account);

        return ResponseEntity.ok().build();
    }
}
