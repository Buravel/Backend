package buravel.buravel.modules.bookmarkpost;

import buravel.buravel.modules.account.AccountDto;
import buravel.buravel.modules.account.AccountService;
import buravel.buravel.modules.bookmark.BookmarkDto;
import buravel.buravel.modules.bookmark.BookmarkRepository;
import buravel.buravel.modules.bookmarkPost.BookmarkPostRepository;
import buravel.buravel.modules.plan.PlanDto;
import buravel.buravel.modules.plan.PlanRepository;
import buravel.buravel.modules.post.PostDto;
import buravel.buravel.modules.post.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class BookmarkPostControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    BookmarkPostRepository bookmarkPostRepository;
    @Autowired
    BookmarkRepository bookmarkRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    PlanRepository planRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AccountService accountService;

    @BeforeEach
    public void setUp(){
        bookmarkPostRepository.deleteAll();
        bookmarkRepository.deleteAll();
        postRepository.deleteAll();
        planRepository.deleteAll();
    }

    @Test
    @DisplayName("북마크 포스트 리스트 조회 - 성공")
    void getBookmarkPosts_ok() throws Exception {

    }

    @Test
    @DisplayName("북마크 포스트 리스트 조회 - 북마크 폴더없음 에러")
    void getBookmarkPosts_wrong() throws Exception {

    }

    @Test
    @DisplayName("북마크 포스트 추가 - 성공")
    void addBookmarkPost_ok() throws Exception {

    }

    @Test
    @DisplayName("북마크 포스트 추가 - 북마크 폴더 없음 에러")
    void addBookmarkPost_wrong_noBookmark() throws Exception {

    }

    @Test
    @DisplayName("북마크 포스트 추가 - 포스트 없음 에러")
    void addBookmarkPost_wrong_noPost() throws Exception {

    }

    @Test
    @DisplayName("북마크 포스트 추가 - 중복 북마크 포스트 존재 에러")
    void addBookmarkPost_wrong_samePost() throws Exception {

    }

    @Test
    @DisplayName("북마크 포스트 삭제 - 성공")
    void deleteBookmarkPost_ok() throws Exception {

    }

    @Test
    @DisplayName("북마크 포스트 삭제 - 북마크 포스트 없음 에러")
    void deleteBookmarkPost_wrong_noBookmarkPost() throws Exception {

    }

    @Test
    @DisplayName("북마크 포스트 플랜 매핑 - 성공")
    void checkBookmarkPost_ok() throws Exception {

    }

    @Test
    @DisplayName("북마크 포스트 플랜 매핑 - 플랜 없음 에러")
    void checkBookmarkPost_wrong_noPlan() throws Exception {

    }

    @Test
    @DisplayName("북마크 포스트 매핑 조회 - 성공")
    void getCheckBookmarkPost_ok() throws Exception {

    }

    @Test
    @DisplayName("북마크 포스트 매핑 조회 - 플랜 없음 에러")
    void getCehckBookmarkPost_wrong_noPlan() throws Exception {

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

    public BookmarkDto createBookmark(){
        BookmarkDto bookmarkDto = new BookmarkDto();
        bookmarkDto.setBookmarkTitle("bookmark");

        return bookmarkDto;
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
