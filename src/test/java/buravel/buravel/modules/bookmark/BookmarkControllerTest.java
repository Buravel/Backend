package buravel.buravel.modules.bookmark;


import buravel.buravel.infra.mail.EmailService;
import buravel.buravel.modules.account.AccountDto;
import buravel.buravel.modules.account.AccountRepository;
import buravel.buravel.modules.account.AccountService;
import buravel.buravel.modules.plan.PlanRepository;
import buravel.buravel.modules.plan.PlanService;
import buravel.buravel.modules.planTag.PlanTagRepository;
import buravel.buravel.modules.post.PostRepository;
import buravel.buravel.modules.postTag.PostTagRepository;
import buravel.buravel.modules.tag.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class BookmarkControllerTest {
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
    @Autowired
    PlanService planService;
    @Autowired
    PlanRepository planRepository;
    @Autowired
    PlanTagRepository planTagRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostTagRepository postTagRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    BookmarkRepository bookmarkRepository;
    @Autowired
    BookmarkService bookmarkService;

    @BeforeEach
    public void setUp(){
        bookmarkRepository.deleteAll();
    }

    @org.junit.Test
    @DisplayName("북마크 생성 성공")
    void createBookmark() throws  Exception{
        String token = getAccessToken();
        mockMvc.perform(post("/bookmark")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(createBookmarkDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("accountResponseDto").exists())
                .andDo(print());
    }

    @org.junit.Test
    @DisplayName("북마크 생성 실패-이미 존재하는 이름")
    void createBookmark_fail() throws  Exception{
        String token = getAccessToken();
        mockMvc.perform(post("/bookmark")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(createBookmarkDto())));
        //같은 북마크 이름으로 한번 더 북마크 생성 수행
        mockMvc.perform(post("/bookmark")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(createBookmarkDto())))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @org.junit.Test
    @DisplayName("북마크 수정 실패 - 자신의 북마크가 아닐 때")
    void modifyBookmark_invalidUser() throws  Exception{
        String token_other = getAccessToken_other();
        mockMvc.perform(post("/bookmark")
                .header(HttpHeaders.AUTHORIZATION, token_other)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(createBookmarkDto())));

        //다른 계정으로 북마크 수정 접근
        String token = getAccessToken();
        mockMvc.perform(patch("/bookmark/{bookmark_id}",2)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(createBookmarkDto())))
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

    @org.junit.Test
    @DisplayName("북마크 수정 실패 - 이미 존재하는 이름")
    void modifyBookmark_invalidName() throws  Exception{

    }

    @Test
    @DisplayName("북마크 삭제 실패 - 자신의 북마크가 아닐 때")
    void deleteBookmark_faild() throws  Exception{

    }


    private BookmarkDto createBookmarkDto() throws Exception{
        BookmarkDto bookmarkDto = new BookmarkDto();
        bookmarkDto.setBookmarkTitle("test");
        return bookmarkDto;
    }

    private String getAccessToken() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("guenwoo");
        accountDto.setEmail("guenwoo@naver.co");
        accountDto.setPassword("12345678");
        accountDto.setNickname("guenwoo");
        accountService.processNewAccount(accountDto);

        ResultActions perform = mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(accountDto)));
        String token = perform.andReturn().getResponse().getHeader("Authorization");
        return token;
    }

    private String getAccessToken_other() throws Exception{
        AccountDto accountDto = new AccountDto();
        accountDto.setUsername("other");
        accountDto.setEmail("other@naver.co");
        accountDto.setPassword("12345678");
        accountDto.setNickname("other");
        accountService.processNewAccount(accountDto);

        ResultActions perform = mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(accountDto)));
        String token = perform.andReturn().getResponse().getHeader("Authorization");
        return token;
    }
}
