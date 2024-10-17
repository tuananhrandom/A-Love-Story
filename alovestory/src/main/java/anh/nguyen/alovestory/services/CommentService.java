package anh.nguyen.alovestory.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import anh.nguyen.alovestory.entities.Comment;
import anh.nguyen.alovestory.repositories.CommentRepository;
import anh.nguyen.alovestory.repositories.PostRepository;

@Service
public class CommentService {
    @Autowired
    PostRepository postRepository;
    CommentRepository commentRepository;
    public boolean createComment(Comment comment){
        if(!commentRepository.existsById(comment.getCommentId())){
            commentRepository.save(comment);
            return true;
        }
        else{
            return false;
        }
    }
    public void deleteComment(Long commentId){
        commentRepository.deleteById(commentId);
    }
    public List<Comment> findCommentInPost(Long postId){
        List<Comment> comments = commentRepository.findByPost_PostIdOrderByCommentDateAsc(postId);
        return comments;
    }
    public List<Comment> findAllComments(){
        return commentRepository.findAll();
    }    
}
