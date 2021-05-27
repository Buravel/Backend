package buravel.buravel.modules.plan;

import buravel.buravel.infra.mail.EmailService;
import buravel.buravel.modules.account.*;
import buravel.buravel.modules.planTag.PlanTagRepository;
import buravel.buravel.modules.post.PostDto;
import buravel.buravel.modules.post.PostRepository;
import buravel.buravel.modules.postTag.PostTagRepository;
import buravel.buravel.modules.tag.TagRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
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
import java.util.List;
import java.util.Random;

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

/**
 * 현재 create-drop으로 설정했기 때문에 시작 시 drop을 먼저한다. 그러나 application.properties에서 사용하는 db는
 * 실행시킨적이 없고 테이블이 구성되어있지 않아서 - drop table if exists를 먼저 수행 이 후 create해서 테이블 생성 후 테스트
 * application.properties에서 buravel db를 사용하지 않고 dialect설정 추가로 플랫폼 지정
 * */
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
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.search").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.updatePlan").exists())
                .andExpect(jsonPath("_links.deletePlan").exists())
                .andExpect(jsonPath("_links.getMyClosedPlans").exists())
                .andExpect(jsonPath("_links.getMyPublishedPlans").exists())
                .andDo(print());

    }

    @Test
    @DisplayName("여행 계획 작성 - 실패(여행 시작날짜가 더 빠를 수 없다.)")
    void createPlan_fail() throws Exception {
        String token = getAccessToken();
        //login하면서 security context holder에 사용자 인증정보 추가했고
        mockMvc.perform(post("/plans")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(createWrongPlanDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
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
                .andExpect(jsonPath("postForPlanResponseDtos").exists())
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.search").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.updatePlan").exists())
                .andExpect(jsonPath("_links.deletePlan").exists())
                .andExpect(jsonPath("_links.createPlan").exists())
                .andExpect(jsonPath("_links.getMyClosedPlans").exists())
                .andExpect(jsonPath("_links.getMyPublishedPlans").exists())
        ;

        Plan plan = planRepository.findById(id).get();
        //post 4개 만들었다
        assertThat(postRepository.countByPlanOf(plan)).isEqualTo(4);
        //planTag 2개 만들어진다
        assertThat(planTagRepository.count()).isEqualTo(2);
    }
    @Test
    @DisplayName("단일 여행 계획 조회 실패")
    void getPlan_fail() throws Exception {
        String token = getAccessToken();
        //login하면서 security context holder에 사용자 인증정보 추가했고
        mockMvc.perform(post("/plans")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(createPlanDto())));
        // 조회
        mockMvc.perform(get("/plans/{id}", 10000))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("내 비공개 여행 계획 조회")
    void getMyClosedPlans()throws Exception {
        String token = setting(false);

        mockMvc.perform(get("/plans/mine/closed")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(jsonPath("_embedded.planResponseDtoList[0].accountResponseDto.username").value("kiseok"))
                .andExpect(jsonPath("_embedded.planResponseDtoList[4].accountResponseDto.username").value("kiseok"))
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]").exists()) // 페이지당 5개
                .andExpect(jsonPath("_embedded.planResponseDtoList[0].published").value(false))
                .andExpect(jsonPath("_embedded.planResponseDtoList[4].published").value(false))
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.search").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.profile").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.self").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.deletePlan").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.updatePlan").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]._links").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]._links.search").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]._links.profile").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]._links.self").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]._links.deletePlan").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]._links.updatePlan").exists())
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.getMyPublishedPlans").exists())
                .andExpect(jsonPath("_links.createPlan").exists())
                .andExpect(jsonPath("page.size").value(5))
                .andExpect(jsonPath("page.totalElements").value(11))
                .andExpect(jsonPath("page.totalPages").value(3))
                .andDo(print());
        Account kiseok = accountRepository.findByUsername("kiseok");
        List<Plan> plans = planRepository.findByPlanManagerAndPublished(kiseok, false);
        assertThat(plans.size()).isEqualTo(11);
    }

    @Test
    @DisplayName("내 공개 여행 계획 조회")
    void getMyPublishedPlans()throws Exception {
        String token = setting(true);

        mockMvc.perform(get("/plans/mine/published")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(jsonPath("_embedded.planResponseDtoList[0].accountResponseDto.username").value("kiseok"))
                .andExpect(jsonPath("_embedded.planResponseDtoList[4].accountResponseDto.username").value("kiseok"))
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]").exists()) // 페이지당 5개
                .andExpect(jsonPath("_embedded.planResponseDtoList[0].published").value(true))
                .andExpect(jsonPath("_embedded.planResponseDtoList[4].published").value(true))
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.search").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.profile").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.self").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.deletePlan").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.updatePlan").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]._links").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]._links.search").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]._links.profile").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]._links.self").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]._links.deletePlan").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[4]._links.updatePlan").exists())
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.getMyClosedPlans").exists())
                .andExpect(jsonPath("_links.createPlan").exists())
                .andExpect(jsonPath("page.size").value(5))
                .andExpect(jsonPath("page.totalElements").value(11))
                .andExpect(jsonPath("page.totalPages").value(3))
                .andDo(print());
        Account kiseok = accountRepository.findByUsername("kiseok");
        List<Plan> plans = planRepository.findByPlanManagerAndPublished(kiseok, true);
        assertThat(plans.size()).isEqualTo(11);
    }

    private String setting(boolean published) throws Exception {
        String token = getAccessToken();
        //login하면서 security context holder에 사용자 인증정보 추가했고
        if (published == true) {
            for (int i = 0; i < 11; i++) {
                mockMvc.perform(post("/plans")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(createPlanDtoWithPublished())));
            }
        } else {
            for (int i = 0; i < 11; i++) {
                mockMvc.perform(post("/plans")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(createPlanDto())));
            }
        }
        return token;
    }
    public PlanDto createPlanDtoWithPublished() {
        Random rand = new Random();
        PlanDto planDto = new PlanDto();
        planDto.setPlanTitle(rand.nextInt(100)+"test");
        planDto.setPublished(true);
        planDto.setStartDate(LocalDate.now());
        planDto.setEndDate(LocalDate.now().plusDays(1));
        planDto.setPlanTag("spring,java");
        planDto.setPostDtos(createPostDtos());
        return planDto;
    }
    public PlanDto createPlanDto() {
        Random rand = new Random();
        PlanDto planDto = new PlanDto();
        planDto.setPlanTitle(rand.nextInt(100)+"test");
        planDto.setPublished(false);
        planDto.setStartDate(LocalDate.now());
        planDto.setEndDate(LocalDate.now().plusDays(1));
        planDto.setPlanTag("spring,java");
        planDto.setPostDtos(createPostDtos());
        return planDto;
    }

    public PlanDto createWrongPlanDto() {
        PlanDto planDto = new PlanDto();
        planDto.setPlanTitle("test");
        planDto.setPublished(false);
        planDto.setStartDate(LocalDate.now());
        planDto.setEndDate(LocalDate.now().minusDays(1));
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
                postDto.setLng(12.345);
                postDto.setLat(54.321);
                postDto.setLocation("우리집");
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