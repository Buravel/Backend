package buravel.buravel.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class SignUpValidator implements Validator {

    private static final String emailRegExp =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern pattern = Pattern.compile(emailRegExp); // 이메일 유효 패턴 저장
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return AccountDto.class.isAssignableFrom(aClass); // 검증할 객체의 class 타입 정보
    }

    @Override
    public void validate(Object target, Errors errors) {

        AccountDto accountDto = (AccountDto) target;

        if(accountDto.getEmail() == null || accountDto.getEmail().trim().isEmpty()) {
            // 이메일 공란 or 공백만 들어올 경우
            errors.rejectValue("email", "required", "You must write email");
        }
        else {
            Matcher matcher = pattern.matcher(accountDto.getEmail());
            // 이메일 형식 검사
            if(!matcher.matches()){
                errors.rejectValue("email", "bad", "Email format error.");
            }
        }

        // 필수 정보 공백 검사
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required", "You must write username");
        ValidationUtils.rejectIfEmpty(errors, "password", "required", "You must write password");

        // 이메일 중복 검사
        Account account = accountRepository.findByEmail(accountDto.getEmail());
        if(account != null){
            errors.rejectValue("email", "bad", "This email is already exists");
        }
    }
}
