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
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("회원가입 에러")
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
    @DisplayName("임시 비밀번호 발급")
    void getTempPassword()throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("kiseok");
        accountDto.setEmail("kisa0828@naver.com");
        accountDto.setPassword("123456789");
        accountDto.setNickname("hello");
        Account account = accountService.processNewAccount(accountDto);
        account.setEmailVerified(true);


        mockMvc.perform(post("/tempPassword")
                .param("email", "kisa0828@naver.com"));
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

        mockMvc.perform(post("/tempPassword")
                .param("email", "kisa0828@naver.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void emailVerification_ok() throws Exception {
        String token = getAccessToken();
        Account user = accountRepository.findByUsername("kiseok");
        String emailCheckToken = user.getEmailCheckToken();
        mockMvc.perform(post("/emailVerification")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("token", emailCheckToken))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("이메일 인증 실패")
    void emailVerification_fail()throws Exception {
        String token = getAccessToken();
        Account user = accountRepository.findByUsername("kiseok");
        String emailCheckToken = user.getEmailCheckToken();
        mockMvc.perform(post("/emailVerification")
                .header(HttpHeaders.AUTHORIZATION, token)
                .param("token", emailCheckToken+"error"))
                .andDo(print())
                .andExpect(status().isBadRequest());
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