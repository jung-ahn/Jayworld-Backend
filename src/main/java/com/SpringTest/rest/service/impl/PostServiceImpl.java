package com.SpringTest.rest.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.SpringTest.rest.model.AppUser;
import com.SpringTest.rest.model.Post;
import com.SpringTest.rest.repo.PostRepo;
import com.SpringTest.rest.service.PostService;
import com.SpringTest.rest.util.Constants;
import com.SpringTest.rest.util.UpDownServiceImpl;

@Service
@Transactional
public class PostServiceImpl implements PostService {

	@Autowired
	private PostRepo postRepo;
	
	@Override
	public Post savePost(AppUser user, HashMap<String, String> request, String postImageName) {
		
		String caption = request.get("caption");
		//String location = request.get("location");
		Post post = new Post();
		post.setName(postImageName);
		post.setCaption(caption);
		//post.setLocation(location);
		post.setUsername(user.getUsername());
		post.setPostedDate(new Date());
		post.setUserImageId(user.getId());
		user.setPost(post);
		postRepo.save(post);
		return post;
	}

	@Override
	public List<Post> postList() {
		return postRepo.findAll();
	}

	@Override
	public Post getPostById(Long id) {
		return postRepo.findPostById(id);
	}

	@Override
	public List<Post> findPostByUsername(String username) {
		return postRepo.findPostByUsername(username);
	}

	@Override
	public Post deletePost(Post post) {
		try {
			Files.deleteIfExists(Paths.get(Constants.POST_FOLDER + "/" + post.getName() + ".png"));
			postRepo.deletePostById(post.getId());  //it is always better to save/delete files on cloud s.a. AWS
			return post;
		} catch (Exception e) {
			return null;
		}		
	}
		
	@Override
	public String savePostImage(MultipartFile multipartFile, String fileName) {
		
		/*
		 * MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)
		 * request; Iterator<String> it = multipartRequest.getFileNames(); MultipartFile
		 * multipartFile = multipartRequest.getFile(it.next());
		 */		 
		try {
			
			InputStream reso = multipartFile.getInputStream();
			String path = (Constants.POST_FOLDER + fileName + ".png");
			new UpDownServiceImpl().upload(reso, path);

		} catch (IOException e) {
			System.out.println("Error occured. Photo not saved!");
			return "Error occured. Photo not saved!";
		}
		System.out.println("Photo saved successfully!");
		return "Photo saved successfully!";
	}

}
