package buravel.buravel.modules.postTag;

import buravel.buravel.modules.post.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface PostTagRepository extends JpaRepository<PostTag,Long> {
    @EntityGraph(value = "PostTag.withTag",type = EntityGraph.EntityGraphType.FETCH)
    List<PostTag> findAllByPost(Post post);

    void deleteAllByPost(Post beforePost);
}
