package buravel.buravel.modules.account.event;

import buravel.buravel.modules.account.Account;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TempPasswordEvent {
    private final Account account;
    private final String tempPassword;
}
