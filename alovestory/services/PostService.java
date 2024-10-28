package anh.nguyen.alovestory.services;

import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.CopyOnWriteArrayList;

import anh.nguyen.alovestory.entities.Post;
import anh.nguyen.alovestory.entities.User;
import anh.nguyen.alovestory.repositories.PostRepository;
import anh.nguyen.alovestory.repositories.UserRepository;

@Service
public class PostService {
    private final ObjectMapper objectMapper;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    public PostService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void createNewPost(Post post) {
        postRepository.save(post);
    }

    public void deletePost(Long postId) {
        // Post thisPost = postRepository.findById(postId).get();
        postRepository.deleteById(postId);
    }

    public List<Post> getAllPost() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean postExistById(Long postId) {
        if (postRepository.existsById(postId)) {
            return true;
        } else {
            return false;
        }
    }

    public void likePost(Long postId) {
        if (postExistById(postId)) {
            Post thisPost = postRepository.findById(postId).get();
            Integer newLikeCount = thisPost.getLikeCount() + 1;
            thisPost.setLikeCount(newLikeCount);
            postRepository.save(thisPost);
            sendSseEvent(thisPost, "post-update");
        } else {
            System.err.println("No Post Exist");
        }
    }

    public SseEmitter createSseEmitter() {
        SseEmitter emitter = new SseEmitter(300_000L); // 5 minutes timeout
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    public void sendSseEvent(Post post, String eventName) {
        System.out.println("da vao den sendSse");
        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                // Sử dụng objectMapper đã cấu hình thay vì tạo mới
                String postData = objectMapper.writeValueAsString(post);
                emitter.send(SseEmitter.event().name(eventName).data(postData));
                System.out.println("da gui du lieu");
            } catch (IOException e) {
                deadEmitters.add(emitter);
                System.out.println("loi gui du lieu: " + e.getMessage());
            }
        });
        System.out.println("thoat ra ngoai");
        emitters.removeAll(deadEmitters);
    }

}
