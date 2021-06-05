package buravel.buravel.modules.mypage.validator;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountRepository;
import buravel.buravel.modules.mypage.dtos.UserNicknameRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UpdateUserNicknameValidator implements Validator {
    private final AccountRepository accountRepository;
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(UpdateUserNicknameValidator.class);
    }
    @Override
    public void validate(Object target, Errors errors) { }

    public void validate(Object object, Account account, Errors errors) {
        UserNicknameRequestDto form = (UserNicknameRequestDto) object;
        Account current = accountRepository.findById(account.getId()).get();
        Account updateUser = accountRepository.findByNickname(form.getNickname());

        if (updateUser != null && current.getNickname().equals(updateUser.getNickname())) {
            errors.rejectValue("nickname","same nickname","현재 닉네임과 동일합니다.");
        } else if (updateUser != null) {
            errors.rejectValue("nickname","invalid nickname",new Object[]{form.getNickname()},"이미 사용중인 닉네임입니다.");
        }
    }
}

