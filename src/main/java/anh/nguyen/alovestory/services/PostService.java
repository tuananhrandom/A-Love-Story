package anh.nguyen.alovestory.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.UUID;
import anh.nguyen.alovestory.entities.Post;
import anh.nguyen.alovestory.entities.User;
import anh.nguyen.alovestory.repositories.PostRepository;
import anh.nguyen.alovestory.repositories.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Pageable;

@Service
public class PostService {
    private static String UPLOAD_DIR = "E:/uploads/";
    @Autowired
    SseService sseService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;

    public void createNewPost(Post post) {
        postRepository.save(post);
    }

    public String saveFile(MultipartFile file) throws IOException {
        // Tạo thư mục upload nếu chưa có
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file và lưu vào thư mục
        String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString() +"."+file.getContentType().split("/")[1];// file type không null được vì đã kiểm tra từ controller
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        // Trả về tên file để lưu vào DBss
        return fileName;
    }

    @Transactional
    public void deletePost(Long postId) {
        try {
            String postMedia = postRepository.findById(postId).get().getPostMedia();
            if (postMedia != null && !postMedia.isEmpty()) {
                String filePath = UPLOAD_DIR + "/" + postMedia;
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
            }
            // commentRepository.deleteAllByPost(postRepository.findById(postId).get());
            postRepository.delete(postRepository.findById(postId).get());
        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Post> getAllPost() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Post findByPostId(Long postId) {
        return postRepository.findById(postId).get();
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
            sseService.sendSseEvent(thisPost, "post-update");
        } else {
            System.err.println("No Post Exist");
        }
    }

    public List<Post> getAllPosts(Pageable pageable) {
        List<Post> checkList = postRepository.findAllByOrderByCreatedAtDesc(pageable).getContent();
        System.out.println("du lieu lay ve frontend: " + checkList);
        return postRepository.findAllByOrderByCreatedAtDesc(pageable).getContent();
    }

}
