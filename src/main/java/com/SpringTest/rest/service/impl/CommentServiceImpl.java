package com.SpringTest.rest.service.impl;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringTest.rest.model.Comment;
import com.SpringTest.rest.model.Post;
import com.SpringTest.rest.repo.CommentRepo;
import com.SpringTest.rest.service.CommentService;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

	@Autowired
	CommentRepo commentRepo;

	@Override
	public void saveComment(Post post, String username, String content) {
		Comment comment = new Comment();
		comment.setContent(content);
		comment.setUsername(username);
		comment.setPostedDate(new Date());
		post.setComments(comment);
		commentRepo.save(comment);
	}

}
