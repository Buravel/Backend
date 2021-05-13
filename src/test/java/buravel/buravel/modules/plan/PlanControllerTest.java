package buravel.buravel.modules.plan;

import buravel.buravel.infra.mail.EmailService;
import buravel.buravel.modules.account.*;
import buravel.buravel.modules.planTag.PlanTagRepository;
import buravel.buravel.modules.post.PostDto;
import buravel.buravel.modules.post.PostRepository;
import buravel.buravel.modules.postTag.PostTagRepository;
import buravel.buravel.modules.tag.TagRepository;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class PlanControllerTest {
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


    @BeforeEach
    public void setUp() {
        tagRepository.deleteAll();
        postTagRepository.deleteAll();
        planTagRepository.deleteAll();
        postRepository.deleteAll();
        planRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("여행 계획 작성_성공")
    void createPlan() throws Exception {
        String token = getAccessToken();
        //login하면서 security context holder에 사용자 인증정보 추가했고
        mockMvc.perform(post("/plans")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(createPlanDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("accountResponseDto").exists())
                .andExpect(jsonPath("planTagResponseDtos").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("단일 여행 계획 조회")
    void getPlan() throws Exception {
        String token = getAccessToken();
        //login하면서 security context holder에 사용자 인증정보 추가했고
        mockMvc.perform(post("/plans")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(createPlanDto())));
        Long id = planRepository.findAll().get(0).getId();

        // 조회
        mockMvc.perform(get("/plans/{id}", id))
                .andDo(print())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("accountResponseDto").exists())
                .andExpect(jsonPath("planTagResponseDtos").exists())
                .andExpect(jsonPath("postForPlanResponseDtos").exists());
        Plan plan = planRepository.findById(id).get();
        //post 4개 만들었다
        assertThat(postRepository.countByPlanOf(plan)).isEqualTo(4);
        //planTag 2개 만들어진다
        assertThat(planTagRepository.count()).isEqualTo(2);
    }


    public PlanDto createPlanDto() {
        PlanDto planDto = new PlanDto();
        planDto.setPlanTitle("test");
        planDto.setPublished(false);
        planDto.setStartDate(LocalDate.now());
        planDto.setEndDate(LocalDate.now().plusDays(1));
        planDto.setPlanTag("spring,java");
        planDto.setPostDtos(createPostDtos());
        return planDto;
    }

    private PostDto[][] createPostDtos() {
        PostDto[][] postDtos = new PostDto[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                PostDto postDto = new PostDto();
                postDto.setPostTitle("posts");
                postDto.setPrice(i+10000l);
                postDto.setCategory("ETC");
                postDto.setRating(4.0f);
                postDto.setLog(12.345);
                postDto.setLat(54.321);
                postDto.setTags("posts,tag");
                postDtos[i][j] = postDto;
            }
        }
        return postDtos;
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