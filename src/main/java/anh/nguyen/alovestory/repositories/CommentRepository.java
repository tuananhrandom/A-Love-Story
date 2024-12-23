package anh.nguyen.alovestory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import anh.nguyen.alovestory.entities.Comment;
import anh.nguyen.alovestory.entities.Post;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_PostIdOrderByCommentTimeDesc(Long postId);

    void deleteAllByPost(Post post);
}
