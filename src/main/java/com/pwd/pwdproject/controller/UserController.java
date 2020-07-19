package com.pwd.pwdproject.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pwd.pwdproject.dao.UserRepo;
import com.pwd.pwdproject.entity.User;
import com.pwd.pwdproject.util.EmailUtil;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

	@Autowired
	private UserRepo userRepo;
	
	private PasswordEncoder pwEncoder = new BCryptPasswordEncoder();
	
	@Autowired
	private EmailUtil emailUtil;
	
	// Get Semua User
	@GetMapping("/readUser")
	public Iterable<User> getAllUser(){
		return userRepo.findAll();
	}
	// Get salah satu user
	@GetMapping("/readUser/{id}")
	public Optional<User> getUserById(@PathVariable int id){
		return userRepo.findById(id);
	}
	// Post User / Regis User
	@PostMapping
	public User addUser(@RequestBody User user) {
		if (user.getUsername().equals("")) {
			throw new RuntimeException("Username masih kosong");
		}
		else if (user.getEmail().equals("")) {
			throw new RuntimeException("Email masih kosong");
		}
		else if (user.getPassword().equals("")) {
			throw new RuntimeException("Password masih kosong");
		}
		Optional<User> findUser = userRepo.findByUsername(user.getUsername());
		
		Optional<User> findEmail = userRepo.findByEmail(user.getEmail());
		
		if(findUser.toString() != "Optional.empty") {
			throw new RuntimeException("Username exists!");
		}
		if (findEmail.toString() != "Optional.empty") {
			throw new RuntimeException("Email exists!");
		} 
		else {
			String encodedPassword = pwEncoder.encode(user.getPassword());
			String verifyToken = pwEncoder.encode(user.getUsername()+ user.getPassword());
			
			user.setPassword(encodedPassword);
			user.setVerified(false);
			user.setRole("user");
			//Simpan verifyToken di database
			user.setVerifyToken(verifyToken);
			
			User savedUser = userRepo.save(user);
			savedUser.setPassword(null);
			
			String linkToVerify = "http://localhost:8080/users/verify/" + user.getUsername() + "?token=" + verifyToken;
			
			String message = "<h1>Selamat! Registrasi Berhasil</h1>\n";
			message += "Hello, " + user.getUsername();
			message += "<p>Please verify your account below for the Gadget Indonesia</p>";
			message += "<p> <a style=\"text-decoration: none;\" href=\""+linkToVerify+"\"> <input style=\"background-color: red;width: 250px;color: white;height: 50px;border: none;border-radius: 5cm;\" type=\"button\" value=\"VERIFY ACCOUNT\"></a> </p>";
			message += "<p>If this wasn't you,please ignore this email.</p>";
			message += "<p>Thanks,Gadget Indonesia</p>";
			emailUtil.sendEmail(user.getEmail(), "Registrasi Akun", message);
			
			return savedUser;
		}
	}
	
	// verifikasi email user
	@GetMapping("/verify/{username}")
	public String verifyUserEmail (@PathVariable String username, @RequestParam String token) {
		User findUser = userRepo.findByUsername(username).get();
		
		if (findUser.getVerifyToken().equals(token)) {
			findUser.setVerified(true);
		} else {
			throw new RuntimeException("Token is invalid");
		}
		
		userRepo.save(findUser);
		
		return "Sukses! akun anda terverifikasi";
	}
	// untuk kirim email lagi untuk verifikasi di profile screen
	@GetMapping("/verifyProfile/{username}")
	public String verifyUserFromProfile(@PathVariable String username) {
		User findUser = userRepo.findByUsername(username).get();
		String linkToVerify = "http://localhost:8080/users/verify/" + findUser.getUsername() + "?token=" + findUser.getVerifyToken();
		String message = "<h1>Selamat! Registrasi Berhasil</h1>\n";
		message += "Hello, " + findUser.getUsername();
		message += "<p>Please verify your account below for the Gadget Indonesia</p>";
		message += "<p> <a style=\"text-decoration: none;\" href=\""+linkToVerify+"\"> <input style=\"background-color: red;width: 250px;color: white;height: 50px;border: none;border-radius: 5cm;\" type=\"button\" value=\"VERIFY ACCOUNT\"></a> </p>";
		message += "<p>If this wasn't you,please ignore this email.</p>";
		message += "<p>Thanks,Gadget Indonesia</p>";
		emailUtil.sendEmail(findUser.getEmail(), "Registrasi Akun", message);
		
		return "Silakan Cek Email";
	}
	
	// login user
	@PostMapping("/login")
	public User loginUser(@RequestBody User user) {
		Optional<User> findUser = userRepo.findByUsername(user.getUsername());
		
							// Password raw/sblm encode  |  password sdh encode
		if (findUser.toString() != "Optional.empty" ) {
			if(pwEncoder.matches(user.getPassword(), findUser.get().getPassword())) {
				findUser.get().setPassword(null);
				return findUser.get();
			}else {
				throw new RuntimeException("Wrong password!");
			}	
		}
		else {
			throw new RuntimeException("Wrong username!");	
		}
		
	}
	
	// Get User untuk lupa password findby username
	@GetMapping("/forgetPass/{username}")
	public User verifikasiForForgetPass(@PathVariable String username) {
		Optional<User> findUsername = userRepo.findByUsername(username);
		if(findUsername.toString() == "Optional.empty")
			throw new RuntimeException("Username doesn't Exist");
		if(findUsername.get().isVerified() == true) {
			String verifyToken = pwEncoder.encode(findUsername.get().getUsername());
			String linkToVerify = "http://localhost:3000/forgetPass/"+ findUsername.get().getUsername()+"/"+ verifyToken.substring(15,20);
			String message = "<p>Hello "+findUsername.get().getUsername()+",</p>";
			message += "<p>We received a request to reset your password for your Gadget Indonesia account</p>";
			message += "<p> <a style=\"text-decoration: none;\" href=\""+linkToVerify+"\"> <input style=\"background-color: red;width: 250px;color: white;height: 50px;border: none;border-radius: 5cm;\" type=\"button\" value=\"RESET PASSWORD\"></a> </p>";	
			message += "<p>mohon kirim email lagi apabila gagal.</p>";
			message += "<p>If this wasn't you,please ignore this email.</p>";
			message += "<p>Thanks,Gadget Indonesia</p>";
			emailUtil.sendEmail(findUsername.get().getEmail(), "Verifikasi Ganti Password", message);
			return findUsername.get();
		}
		throw new RuntimeException("Username doesn't  Verified!");
	}
	
	// edit password setelah klik email
	@PutMapping("/editForgetPass")
	public User editLupaPassword(@RequestBody User user) {
		User findUsername = userRepo.findByUsername(user.getUsername()).get();
		user.setId(findUsername.getId());
		user.setAddress(findUsername.getAddress());
		user.setEmail(findUsername.getEmail());
		user.setFullName(findUsername.getFullName());
		user.setVerified(true);
		user.setNoTelp(findUsername.getNoTelp());
		user.setRole(findUsername.getRole());
		user.setUsername(findUsername.getUsername());
		user.setVerifyToken(findUsername.getVerifyToken());
		String encodedPassword = pwEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		User savedUser = userRepo.save(user);
		savedUser.setPassword(null);
		return savedUser;
	}
	
	// edit Profile User
	@PutMapping("/editProfile")
	public User editProfile(@RequestBody User user) {
		User findUser = userRepo.findById(user.getId()).get();
		String usernameTemp = findUser.getUsername();
		String emailTemp = findUser.getEmail();
		findUser.setUsername(null);
		findUser.setEmail(null);
		userRepo.save(findUser);
		Optional<User> findUsername = userRepo.findByUsername(user.getUsername());
		Optional<User> findEmail = userRepo.findByEmail(user.getEmail());
		if(findUsername.toString() != "Optional.empty") {
			findUser.setUsername(usernameTemp);
			findUser.setEmail(emailTemp);
			userRepo.save(findUser);
			throw new RuntimeException("Username exists!");
		}
		if (findEmail.toString() != "Optional.empty") {
			findUser.setUsername(usernameTemp);
			findUser.setEmail(emailTemp);
			userRepo.save(findUser);
			throw new RuntimeException("Email exists!");
		}else {
			findUser.setUsername(user.getUsername());
			findUser.setEmail(user.getEmail());
			userRepo.save(findUser);
			user.setRole(findUser.getRole());
			user.setVerifyToken(findUser.getVerifyToken());
			user.setVerified(findUser.isVerified());
			user.setPassword(findUser.getPassword());
			User savedUser = userRepo.save(user);
			savedUser.setPassword(null);
			return savedUser;
		}
	}
	
	@GetMapping("/pass/{id}/{oldPassword}/{newPassword}")
	public User editPass(@PathVariable int id , @PathVariable String oldPassword,@PathVariable String newPassword) {
		User findUser = userRepo.findById(id).get();
		if (pwEncoder.matches(oldPassword, findUser.getPassword())) {
			String encodedPassword = pwEncoder.encode(newPassword);
			findUser.setPassword(encodedPassword);
			User savedUser = userRepo.save(findUser);
			savedUser.setPassword(null);
			return savedUser;
		}
		throw new RuntimeException("Password Lama Tidak Sama");
	}
}
