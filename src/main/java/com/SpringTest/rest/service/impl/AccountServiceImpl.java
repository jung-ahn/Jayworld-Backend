package com.SpringTest.rest.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.SpringTest.rest.model.AppUser;
import com.SpringTest.rest.model.Role;
import com.SpringTest.rest.model.UserRole;
import com.SpringTest.rest.repo.AppUserRepo;
import com.SpringTest.rest.repo.RoleRepo;
import com.SpringTest.rest.service.AccountService;
import com.SpringTest.rest.util.Constants;
import com.SpringTest.rest.util.EmailConstructor;
import com.SpringTest.rest.util.UpDownServiceImpl;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

	@Autowired
	AccountService accountService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private AppUserRepo appUserRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private EmailConstructor emailConstructor;

	@Autowired
	private JavaMailSender mailSender;
	

	@Override
	@Transactional
	public AppUser saveUser(String name, String username, String email) {
		String password = RandomStringUtils.randomAlphanumeric(10);
		String encryptedPassword = bCryptPasswordEncoder.encode(password);
		AppUser appUser = new AppUser();
		appUser.setPassword(encryptedPassword);
		appUser.setName(name);
		appUser.setUsername(username);
		appUser.setEmail(email);
		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(new UserRole(appUser, accountService.findUserRoleByName("USER")));
		appUser.setUserRoles(userRoles);
		appUserRepo.save(appUser);
		
		try {	
			InputStream reso = new UpDownServiceImpl().download(Constants.TEMP_USER);		
			String fileName = appUser.getId() + ".png";
			String path = (Constants.USER_FOLDER + fileName);
			new UpDownServiceImpl().upload(reso, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mailSender.send(emailConstructor.constructNewUserEmail(appUser, password));
		return appUser;
	}
	
	//will add user with custom password.
	@Override
	public AppUser saveUserWithPassword(String name, String username, String password, String email) {
		AppUser appUser = new AppUser();
		String encryptedPassword = bCryptPasswordEncoder.encode(password);
		appUser.setPassword(encryptedPassword); //get a string of password and then encrypt it.
		appUser.setName(name);
		appUser.setUsername(username);
		appUser.setEmail(email);
		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(new UserRole(appUser, accountService.findUserRoleByName("USER")));
		appUser.setUserRoles(userRoles);
		appUserRepo.save(appUser);
		try {
			//access to temp image file -> read it as inputstream so we can upload. 
			InputStream reso = new UpDownServiceImpl().download(Constants.TEMP_USER);		
			String fileName = appUser.getId() + ".png";
			String path = (Constants.USER_FOLDER + fileName);
			new UpDownServiceImpl().upload(reso, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mailSender.send(emailConstructor.constructNewUserEmail(appUser, password));
		return appUser;
	}
	

	@Override
	public void updateUserPassword(AppUser appUser, String newpassword) {
		String encryptedPassword = bCryptPasswordEncoder.encode(newpassword);
		appUser.setPassword(encryptedPassword);
		appUserRepo.save(appUser);
		mailSender.send(emailConstructor.constructResetPasswordEmail(appUser, newpassword));
	}

	@Override
	public Role saveRole(Role role) {
		return roleRepo.save(role);
	}

	@Override
	public AppUser findByUsername(String username) {
		return appUserRepo.findByUsername(username);
	}

	@Override
	public AppUser findByEmail(String userEmail) {
		return appUserRepo.findByEmail(userEmail);
	}

	@Override
	public List<AppUser> userList() {
		return appUserRepo.findAll();
	}

	@Override
	public Role findUserRoleByName(String name) {
		return roleRepo.findRoleByName(name);
	}

	@Override
	public AppUser simpleSaveUser(AppUser user) {
		appUserRepo.save(user);
		return user;

	}

	@Override
	public AppUser updateUser(AppUser user, HashMap<String, String> request) {
		String name = request.get("name");
		// String username = request.get("username");
		String email = request.get("email");
		String bio = request.get("bio");
		user.setName(name);
		// appUser.setUsername(username);
		user.setEmail(email);
		user.setBio(bio);
		appUserRepo.save(user);
		return user;

	}

	@Override
	public AppUser findUserById(Long id) {
		return appUserRepo.findUserById(id);
	}

	@Override
	public void deleteUser(AppUser appUser) {
		appUserRepo.delete(appUser);

	}

	@Override
	public void resetPassword(AppUser user) {
		String password = RandomStringUtils.randomAlphanumeric(10);
		String encryptedPassword = bCryptPasswordEncoder.encode(password);
		user.setPassword(encryptedPassword);
		appUserRepo.save(user);
		mailSender.send(emailConstructor.constructResetPasswordEmail(user, password));

	}

	@Override
	public List<AppUser> getUsersListByUsername(String username) {
		return appUserRepo.findByUsernameContaining(username);
	}

	@Override
	public String saveUserImage(MultipartFile multipartFile, Long userImageId) {
		/*
		 * MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)
		 * request; Iterator<String> it = multipartRequest.getFileNames(); MultipartFile
		 * multipartFile = multipartRequest.getFile(it.next());
		 */
		
		try {	
			Files.deleteIfExists(Paths.get(Constants.USER_FOLDER + "/" + userImageId + ".png"));
			InputStream reso = multipartFile.getInputStream();
			String path = (Constants.USER_FOLDER + userImageId + ".png");
			new UpDownServiceImpl().upload(reso, path);
			
			//resize profile image to reduce bandwitdh

			/*
			 * InputStream input = multipartFile.getInputStream(); BufferedImage image =
			 * ImageIO.read(input); BufferedImage resized = (resize(image,100,100)); File
			 * output = new File(Constants.USER_FOLDER + "/" + userImageId +
			 * "_resized.png"); ByteArrayOutputStream resi = new ByteArrayOutputStream();
			 * ImageIO.write(resized, "png", resi);
			 * 
			 * InputStream target = ByteArrayOutputStream(resi.toByteArray());
			 */
			
			return "User picture saved to server";
		} catch (IOException e) {
			return "User picture Saved";
		}
	}



}
