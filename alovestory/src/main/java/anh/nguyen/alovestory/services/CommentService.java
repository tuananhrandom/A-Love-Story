package anh.nguyen.alovestory.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import anh.nguyen.alovestory.entities.Comment;
import anh.nguyen.alovestory.entities.Post;
import anh.nguyen.alovestory.repositories.CommentRepository;
import anh.nguyen.alovestory.repositories.PostRepository;

@Service
public class CommentService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    public boolean createComment(Comment comment){
        if(!commentRepository.existsById(comment.getCommentId())){
            commentRepository.save(comment);
            Post thisPost = comment.getPost();
            Long thisPostId=thisPost.getPostId();
            addCommentCountToPost(thisPostId);
            return true;
        }
        else{
            return false;
        }
    }
    public void deleteComment(Long commentId){
        commentRepository.deleteById(commentId);
        subCommentCountToPost(commentId);
    }
    public List<Comment> findCommentInPost(Long postId){
        List<Comment> comments = commentRepository.findByPost_PostIdOrderByCommentDateAsc(postId);
        return comments;
    }
    public List<Comment> findAllComments(){
        return commentRepository.findAll();
    }
    public boolean commentExist(Long commentId){
        if(commentRepository.existsById(commentId)){
            return true;
        }else{
            return false;
        }
    }










    // khi một bình có một bình luận mới, hàm này sẽ được gọi
    //và thêm một số đếm cho comment nằm trong post này
    public void addCommentCountToPost(Long postId){
        if(postRepository.existsById(postId)){
            Post post = postRepository.findById(postId).get();
            Integer newCommentCount = post.getCommentCount() + 1;
            post.setCommentCount(newCommentCount);
            postRepository.save(post);
        }
    }
    //khi xóa bình luận thì ta có thể giảm bớt một comment
    public void subCommentCountToPost(Long postId) {
        if (postRepository.existsById(postId)) {
            Post post = postRepository.findById(postId).get();
            Integer newCommentCount = post.getCommentCount() - 1;
            // Đảm bảo số lượng comment không âm
            if (newCommentCount >= 0) { 
                post.setCommentCount(newCommentCount);
                postRepository.save(post);
            }
        }
    }

}
