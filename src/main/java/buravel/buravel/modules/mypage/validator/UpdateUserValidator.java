package buravel.buravel.modules.mypage.validator;

import buravel.buravel.modules.account.AccountDto;
import buravel.buravel.modules.account.AccountRepository;
import buravel.buravel.modules.mypage.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UpdateUserValidator implements Validator {
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(UserRequestDto.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        UserRequestDto form = (UserRequestDto) object;
        if (accountRepository.existsByNickname(form.getNickname())) {
            errors.rejectValue("nickname", "invalid nickname", new Object[]{form.getNickname()}, "이미 사용중인 닉네임입니다.");
        }
    }

    public void isRegexPassword(String password, Errors errors) {
        String regex = "^[a-z0-9]{8,40}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if (!matcher.find()) {
            errors.rejectValue("password", "invalid password", "영소문자, 숫자, 길이가 8에서 40 사이여야 합니다.");
        }
    }
}
