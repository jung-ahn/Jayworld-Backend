package com.SpringTest.rest.service;

import com.SpringTest.rest.model.Post;

public interface CommentService {

	public void saveComment(Post post, String username, String content);

}
