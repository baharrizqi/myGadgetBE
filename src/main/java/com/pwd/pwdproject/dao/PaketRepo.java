package com.pwd.pwdproject.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pwd.pwdproject.entity.Paket;

public interface PaketRepo extends JpaRepository<Paket, Integer> {
	public Optional<Paket> findByPaketName(String paketName);

}
