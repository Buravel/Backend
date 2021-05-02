package buravel.buravel.modules.bookmarkPost;

import buravel.buravel.modules.bookmark.Bookmark;
import buravel.buravel.modules.plan.Plan;
import buravel.buravel.modules.post.Post;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class BookmarkPost {
    @Id
    @GeneratedValue
    @Column(name = "bookmark_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookmark_id")
    private Bookmark bookmark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private boolean checked;
    // 나의 여행으로 가져갈 때 채크하면 check == true
    //이 후  bookmarkPostManager가 !나! 이면서 checked == true인 bookmarkPost들을 가져오고 드래그로 끌어다 plan에 추가 등

    @ManyToOne(fetch = FetchType.LAZY)
    private Plan planOf;


}