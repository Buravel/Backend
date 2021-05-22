package buravel.buravel.modules.account.validator;

import buravel.buravel.modules.account.AccountDto;
import buravel.buravel.modules.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(AccountDto.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        AccountDto form = (AccountDto) object;
        if (accountRepository.existsByUsername(form.getUsername())) {
            errors.rejectValue("username", "invalid username", new Object[]{form.getUsername()}, "이미 사용중인 아이디입니다.");
        }
        if (accountRepository.existsByEmail(form.getEmail())) {
            errors.rejectValue("email","invalid email",new Object[]{form.getEmail()},"이미 사용중인 이메일입니다.");
        }
        if (accountRepository.existsByNickname(form.getNickname())) {
            errors.rejectValue("nickname","invalid nickname",new Object[]{form.getNickname()},"이미 사용중인 닉네임입니다.");
        }
    }
}
