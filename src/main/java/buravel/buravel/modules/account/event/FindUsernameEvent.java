package buravel.buravel.modules.account.event;

import buravel.buravel.modules.account.Account;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FindUsernameEvent {
    private final Account account;
}
