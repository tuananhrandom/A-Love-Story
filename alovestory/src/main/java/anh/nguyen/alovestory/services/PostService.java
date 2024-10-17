package anh.nguyen.alovestory.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import anh.nguyen.alovestory.entities.Post;
import anh.nguyen.alovestory.entities.User;
import anh.nguyen.alovestory.repositories.PostRepository;
import anh.nguyen.alovestory.repositories.UserRepository;

@Service
public class PostService {
    @Autowired
    PostRepository postRepository;
    UserRepository userRepository;
    public boolean createNewPost(Post post){
        if(!postRepository.existsById(post.getPostId())){
            postRepository.save(post);
            return true;
        }
        else{
            return false;
        }
    }
    public void deletePost(Long postId){
        // Post thisPost = postRepository.findById(postId).get();
        postRepository.deleteById(postId);
    }
    public List<Post> getAllPost(){
        return postRepository.findAll();
    }
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public boolean postExistById(Long postId){
        if(postRepository.existsById(postId)){
            return true;
        }
        else{
            return false;
        }
    }
    
}
