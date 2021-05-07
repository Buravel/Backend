package buravel.buravel.modules.post;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.bookmarkPost.BookmarkPost;
import buravel.buravel.modules.plan.Plan;
import buravel.buravel.modules.postTag.PostTag;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Post {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String postTitle;

    @Column(nullable = false)
    private Long price= 0L;

    //보여줄 땐 x.x만원  - 가격입력받을 때  12345원 -> 345버리고 12 string으로 변환 후 중간에 . 추가하는 로직으로 필드값할당
    private String outputPrice;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String postImage;

    @Enumerated(EnumType.STRING)
    private PostCategory category;

    @Column(nullable = false)
    private Float rating;

    private boolean closed;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double log;

    /**
     * 일자별 + 순서별
     */
    private Integer day;
    private Integer ordering;

    private boolean checked;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account postManager;

    @OneToMany(mappedBy = "post")
    private List<BookmarkPost> bookmarkPosts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Plan planOf;

    // 태그는 입력받을 dto에 string으로 입력받고 처리
    @OneToMany(mappedBy = "post")
    private List<PostTag> postTagList = new ArrayList<>();

}