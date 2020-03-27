package com.SpringTest.rest.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SpringTest.rest.model.Comment;


public interface CommentRepo extends JpaRepository<Comment, Long> {

}
