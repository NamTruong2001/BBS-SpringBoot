package com.truong.bbs_springboot.repository;

import com.truong.bbs_springboot.model.Post;
import com.truong.bbs_springboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsByUser(User user);

    boolean existsById(Long Id);

    Optional<Post> getPostByIdAndUser(Long id, User user);
}
