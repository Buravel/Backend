package buravel.buravel.modules;

import buravel.buravel.infra.mail.EmailService;
import buravel.buravel.modules.account.dtos.AccountDto;
import buravel.buravel.modules.account.AccountRepository;
import buravel.buravel.modules.account.AccountService;
import buravel.buravel.modules.plan.Plan;
import buravel.buravel.modules.plan.dtos.PlanDto;
import buravel.buravel.modules.plan.PlanRepository;
import buravel.buravel.modules.plan.PlanService;
import buravel.buravel.modules.planTag.PlanTagRepository;
import buravel.buravel.modules.post.dtos.PostDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerTest {
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
    @DisplayName("?????? - ?????? ?????? x")
    void searchWithoutPrice() throws Exception{
        setting();
        mockMvc.perform(get("/index/search")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                //?????????!!! ?????? plan->published true
                .param("keyword", "test")
                .param("min", "0")
                .param("max", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.planResponseDtoList").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0].id").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0].accountResponseDto").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0].planTagResponseDtos").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.search").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.profile").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.self").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.deletePlan").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.updatePlan").exists())
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.first").exists())
                .andExpect(jsonPath("_links.last").exists())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("page.totalElements").value("11"))
                .andExpect(jsonPath("page.totalPages").value("2"));

        assertThat(planRepository.count()).isEqualTo(11);
        assertThat(postRepository.count()).isEqualTo(44);
    }



    @Test
    @DisplayName("?????? - ?????? ?????? o")
    void searchWithPrice() throws Exception {
        setting();
        // ????????? plan??? totalPrice??? 100????????? ??????
        Plan plan = planRepository.findAll().get(0);
        plan.setTotalPrice(100l);

        mockMvc.perform(get("/index/search")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                //?????????!!! ?????? plan->published true
                .param("keyword", "test")
                .param("min", "0")
                //max setting
                .param("max", "200"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.planResponseDtoList").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0].id").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0].accountResponseDto").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0].planTagResponseDtos").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.search").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.profile").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.self").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.deletePlan").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.updatePlan").exists())
                // ?????? ?????? ????????? ???????????? plan??? ??? 1???
                .andExpect(jsonPath("_embedded.planResponseDtoList[1].id").doesNotExist())
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("page.totalElements").value("1"))
                .andExpect(jsonPath("page.totalPages").value("1"));
    }

    @Test
    @DisplayName("?????? - ?????? ?????? ????????? ????????? plan ?????? ????????? ????????? ??? ??????.")
    void searchWithoutCond() throws Exception{
        setting();
        mockMvc.perform(get("/index/search")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                //?????????!!! ?????? plan->published true
                .param("keyword", "")
                .param("min", "0")
                .param("max", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.planResponseDtoList").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0].id").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0].accountResponseDto").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0].planTagResponseDtos").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.search").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.profile").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.self").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.deletePlan").exists())
                .andExpect(jsonPath("_embedded.planResponseDtoList[0]._links.updatePlan").exists())
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.first").exists())
                .andExpect(jsonPath("_links.last").exists())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("page.totalElements").value("11"))
                .andExpect(jsonPath("page.totalPages").value("2"));

        assertThat(planRepository.count()).isEqualTo(11);
        assertThat(postRepository.count()).isEqualTo(44);
    }

    private void setting() throws Exception {
        String token = getAccessToken();
        //login????????? security context holder??? ????????? ???????????? ????????????
        for (int i = 0; i < 11; i++) {
            mockMvc.perform(post("/plans")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(createPlanDto(i))));
            }
        }

    public PlanDto createPlanDto(int i) {
        PlanDto planDto = new PlanDto();
        planDto.setPlanTitle(i+"test");
        planDto.setPublished(false);
        planDto.setPublished(true);
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
                postDto.setLng(12.345);
                postDto.setLat(54.321);
                postDto.setLocation("?????????");
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