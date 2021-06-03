package buravel.buravel.modules.account;

import buravel.buravel.infra.AppProperties;
import buravel.buravel.infra.mail.EmailMessage;
import buravel.buravel.infra.mail.EmailService;
import buravel.buravel.modules.account.event.FindUsernameEvent;
import buravel.buravel.modules.account.event.SignUpConfirmEvent;
import buravel.buravel.modules.account.event.TempPasswordEvent;
import buravel.buravel.modules.plan.Plan;
import buravel.buravel.modules.plan.PlanRepository;
import buravel.buravel.modules.plan.PlanResponseDto;
import buravel.buravel.modules.plan.PlanService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final PlanRepository planRepository;
    private final PlanService planService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(s);
        if (account == null) {
            throw new UsernameNotFoundException(s);
        }
        return new UserAccount(account);
    }

    // signUp

    public Account processNewAccount(AccountDto accountDto) {
        Account account = saveNewAccount(accountDto);
        sendSignUpConfirmEmail(account);
        return account;
    }
    // save account
    public Account saveNewAccount(AccountDto accountDto) {
        Account map = modelMapper.map(accountDto, Account.class);
        map.setPassword(passwordEncoder.encode(map.getPassword()));
        map.generateEmailCheckToken();
        Account saved = accountRepository.save(map);
        return saved;
    }
    public void sendTempPassword(Account account) {
        if (!account.isEmailVerified()) {
            throw new AccessDeniedException("이메일 인증된 회원만 가능합니다.");
        }
        // temp pass create
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        uuid = uuid.substring(0, 10);
        // set temp pass
        account.setPassword(passwordEncoder.encode(uuid));
        publisher.publishEvent(new TempPasswordEvent(account,uuid));

    }
    // resend emailCheckToken
    private void sendSignUpConfirmEmail(Account account) {
        publisher.publishEvent(new SignUpConfirmEvent(account));
    }

    public void reSendEmailCheckToken(Account account) {
        Account ac = accountRepository.findById(account.getId()).get();
        ac.generateEmailCheckToken();
        Account saved = accountRepository.save(ac);
        sendSignUpConfirmEmail(saved);
    }

    public AccountResponseDto emailVerification(Account account, String token){
        if (!account.isValidToken(token)) {
            return null;
        }

        completeSignUp(account);
        return createAccountResponseDto(account);
    }

    public void completeSignUp(Account find) {
        find.completeSignUp();
    }

    public AccountResponseDto createAccountResponseDto(Account account){
        AccountResponseDto dto = modelMapper.map(account, AccountResponseDto.class);

        return dto;
    }

    public Account findById(Long id) {
        return accountRepository.findById(id).get();
    }

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }


    public void sendUsername(Account account){
        if (!account.isEmailVerified()) {
            throw new AccessDeniedException("이메일 인증된 회원만 가능합니다.");
        } // not verified user

        publisher.publishEvent(new FindUsernameEvent(account));
    }

    public AccountWithPlanDto createResponseWithPlan(Account account) {
        AccountWithPlanDto result = new AccountWithPlanDto();
        result.setAccount(account);
        Plan plan  = planRepository.findPlanSoon(account);
        if (plan == null) {
            result.setPlanResponseDto(null);
            return result;
        } else {
            PlanResponseDto planResponse = planService.createPlanResponse(account, plan);
            result.setPlanResponseDto(planResponse);
            return result;
        }
    }
}
