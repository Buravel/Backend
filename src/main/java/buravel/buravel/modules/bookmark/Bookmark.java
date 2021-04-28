package buravel.buravel.modules.bookmark;

import buravel.buravel.modules.account.Account;
import buravel.buravel.modules.bookmarkPost.BookmarkPost;
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
public class Bookmark {
    @Id
    @GeneratedValue
    @Column(name = "bookmark_id")
    private Long id;

    private String bookmarkTitle; // 북마크폴더명

    @ManyToOne(fetch = FetchType.LAZY)
    private Account bookmarkManager;

    @OneToMany(mappedBy = "bookmark")
    private List<BookmarkPost> bookmarkPosts = new ArrayList<>();

}
