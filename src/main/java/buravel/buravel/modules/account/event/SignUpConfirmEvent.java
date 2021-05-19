package buravel.buravel.modules.account.event;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.account.AccountDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignUpConfirmEvent {
    private final String email;
    private final String token;
}
