package buravel.buravel.modules.account;

import buravel.buravel.infra.mail.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @MockBean
    EmailService emailService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AccountService accountService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ModelMapper modelMapper;


    @BeforeEach
    public void setUp() {
        accountRepository.deleteAll();
    }


    @Test
    @DisplayName("회원가입 정상")
    void signUp_correct() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("kiseok");
        accountDto.setEmail("kisa0828@naver.com");
        accountDto.setPassword("123456789");
        accountDto.setNickname("hello");

        mockMvc.perform(post("/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.search").exists())
                .andExpect(jsonPath("_links.create-plan").exists())
                .andExpect(jsonPath("_links.myPage").exists())
                .andExpect(jsonPath("_links.my-closed-plans").exists())
                .andExpect(jsonPath("_links.my-published-plans").exists());
    }
    @Test
    @DisplayName("회원가입 에러- 아이디,이메일 이미 사용중")
    void signUp_wrong() throws Exception {
        Account kiseok = Account.builder()
                .username("kiseok")
                .password(passwordEncoder.encode("123456789"))
                .email("kisa0828@naver.com")
                .build();
        accountRepository.save(kiseok);

        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("kiseok");
        accountDto.setPassword("123456789");
        accountDto.setEmail("kisa0828@naver.com");
        accountDto.setNickname("hello");

        mockMvc.perform(post("/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors").exists());
    }
    @Test
    @DisplayName("회원가입 에러- 패스워드 길이는 8~20 사이")
    void signUp_wrongPass() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("kiseok");
        accountDto.setPassword("12");
        accountDto.setEmail("kisa0828@naver.com");
        accountDto.setNickname("hello");

        mockMvc.perform(post("/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors").exists());
    }

    @Test
    @DisplayName("로그인 정상")
    void login() throws Exception {
        Account account = new Account();
        account.setUsername("kiseok");
        account.setEmail("kisa0828@naver.com");
        account.setPassword(passwordEncoder.encode("123456789"));
        account.setNickname("hello");

        accountRepository.save(account);

        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("kiseok");
        accountDto.setPassword("123456789");

        mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 틀림")
    void login_fail_pass() throws Exception {
        Account account = new Account();
        account.setUsername("kiseok");
        account.setEmail("kisa0828@naver.com");
        account.setPassword(passwordEncoder.encode("123456789"));
        account.setNickname("hello");

        accountRepository.save(account);

        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("kiseok");
        accountDto.setPassword("12345678");

        mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("로그인 실패 - id틀림")
    void login_fail_id() throws Exception {
        Account account = new Account();
        account.setUsername("kiseok");
        account.setEmail("kisa0828@naver.com");
        account.setPassword(passwordEncoder.encode("123456789"));
        account.setNickname("hello");

        accountRepository.save(account);

        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("kiseok1");
        accountDto.setPassword("123456789");

        mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("임시 비밀번호 발급")
    void getTempPassword()throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("kiseok");
        accountDto.setEmail("kisa0828@naver.com");
        accountDto.setPassword("123456789");
        accountDto.setNickname("hello");
        Account account = accountService.processNewAccount(accountDto);
        account.setEmailVerified(true);
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail("kisa0828@naver.com");
        mockMvc.perform(post("/tempPassword")
                .content(objectMapper.writeValueAsString(emailDto)));
        Account user = accountRepository.findByEmail("kisa0828@naver.com");
        assertThat(user.getPassword().matches("123456789")).isFalse();

    }
    @Test
    @DisplayName("임시 비밀번호 발급 에러 - 이메일 인증을 한 회원만 가능")
    void getTempPassword_wrong() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("kiseok");
        accountDto.setEmail("kisa0828@naver.com");
        accountDto.setPassword("123456789");
        accountDto.setNickname("hello");
        Account account = accountService.processNewAccount(accountDto);
        account.setEmailVerified(false);
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail("kisa0828@naver.com");
        mockMvc.perform(post("/tempPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isForbidden());
    }
    @Test
    @DisplayName("임시 비밀번호 발급 에러 - 회원 가입된 이메일만 사용가능")
    void getTempPassword_wrong_withoutSignUp() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail("hello@naver.com");
        mockMvc.perform(post("/tempPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void emailVerification_ok() throws Exception {
        String token = getAccessToken();
        Account user = accountRepository.findByUsername("kiseok");
        String emailCheckToken = user.getEmailCheckToken();
        mockMvc.perform(get("/emailVerification")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("token", emailCheckToken))
                .andExpect(status().isOk());

        assertThat(accountRepository.findByUsername("kiseok").isEmailVerified()).isTrue();
    }
    @Test
    @DisplayName("이메일 인증 실패")
    void emailVerification_fail()throws Exception {
        String token = getAccessToken();
        Account user = accountRepository.findByUsername("kiseok");
        String emailCheckToken = user.getEmailCheckToken();
        mockMvc.perform(get("/emailVerification")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("token", emailCheckToken+"error"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아이디 찾기 메일 발송")
    void getUsername_ok() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("kiseok");
        accountDto.setEmail("may05200@naver.com");
        accountDto.setPassword("123456789");
        accountDto.setNickname("hello");
        Account account = accountService.processNewAccount(accountDto);
        account.setEmailVerified(true);


        mockMvc.perform(get("/findUsername")
                .param("email", "may05200@naver.com"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("아이디 찾기 에러 - 해당 유저 없음")
    void getUsername_wrong_noUser() throws Exception {
        mockMvc.perform(get("/findUsername")
                .param("email", "may05200@naver.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("아이디 찾기 에러 - 이메일 인증된 사용자가 아님")
    void getUsername_wrong_notEmailVerified() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("kiseok");
        accountDto.setEmail("may05200@naver.com");
        accountDto.setPassword("123456789");
        accountDto.setNickname("hello");
        Account account = accountService.processNewAccount(accountDto);
        account.setEmailVerified(false);


        mockMvc.perform(get("/findUsername")
                .param("email", "may05200@naver.com"))
                .andExpect(status().isForbidden());
    }

    private String getAccessToken() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("kiseok");
        accountDto.setEmail("kisa0828@naver.com");
        accountDto.setPassword("123456789");
        accountDto.setNickname("hello");
        accountService.processNewAccount(accountDto);

        ResultActions perform = mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(accountDto)));
        String token = perform.andReturn().getResponse().getHeader("Authorization");
        return token;
    }
}