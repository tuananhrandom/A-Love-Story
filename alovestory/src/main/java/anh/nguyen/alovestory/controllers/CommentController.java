package anh.nguyen.alovestory.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import anh.nguyen.alovestory.entities.Comment;
import anh.nguyen.alovestory.services.CommentService;
import anh.nguyen.alovestory.services.PostService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;

    @GetMapping("/getPostComment/{postId}")
    public List<Comment> getPostComments(@RequestParam Long postId) {
        List<Comment> listComments = commentService.findCommentInPost(postId); 
        return listComments;
    }
    @GetMapping("/all")
    public List<Comment> getAllComment() {
        return commentService.findAllComments();
    }
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@RequestParam Long commentId){
        if(commentService.commentExist(commentId)){
            commentService.deleteComment(commentId);
            return new ResponseEntity<>("Delete Completed",HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Delete Fail",HttpStatus.BAD_REQUEST);
        }
    } 
    
    
}
