package com.pwd.pwdproject.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pwd.pwdproject.entity.User;

public interface UserRepo extends JpaRepository<User, Integer> {
	public Optional<User> findByUsername(String username);
	
	public Optional<User> findByEmail(String email);
}
