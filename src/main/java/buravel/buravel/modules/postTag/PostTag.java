package buravel.buravel.modules.postTag;

import buravel.buravel.modules.post.Post;
import buravel.buravel.modules.tag.Tag;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostTag {
    @Id
    @GeneratedValue
    @Column(name = "postTag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private Tag tag;
}
