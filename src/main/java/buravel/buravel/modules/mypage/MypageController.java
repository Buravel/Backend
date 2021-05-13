package buravel.buravel.modules.mypage;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MypageController {

    @PatchMapping("/mypage/picture")
    public ResponseEntity updateUserPicture(@CurrentUser Account account){
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/account")
    public ResponseEntity deleteAccount(@CurrentUser Account account){
        return ResponseEntity.ok().build();
    }
}
