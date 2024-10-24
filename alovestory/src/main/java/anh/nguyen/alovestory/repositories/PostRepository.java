package anh.nguyen.alovestory.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import anh.nguyen.alovestory.entities.Post;
@Repository
public interface PostRepository extends JpaRepository<Post,Long> { 
    List<Post> findAllByOrderByCreatedAtAsc();
}
