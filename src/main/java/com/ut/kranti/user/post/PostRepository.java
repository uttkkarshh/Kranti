package com.ut.kranti.user.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ut.kranti.user.UserProfile;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByAuthor(UserProfile user);

	List<Post> findAllByOrderByCreatedAtDesc();
	List<Post> findByAuthorIn(List<UserProfile> authors);

	Page<Post> findByAuthor_Id(Long userId, Pageable pageable);

	Page<Post> findByAuthorId(Long userId, Pageable pageable);
}
