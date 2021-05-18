package buravel.buravel.modules.bookmarkpost;

import buravel.buravel.modules.bookmark.BookmarkRepository;
import buravel.buravel.modules.bookmarkPost.BookmarkPostRepository;
import buravel.buravel.modules.plan.PlanRepository;
import buravel.buravel.modules.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
}
