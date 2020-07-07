package com.pwd.pwdproject.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pwd.pwdproject.dao.PaketRepo;
import com.pwd.pwdproject.entity.Paket;

@RestController
@RequestMapping("/paket")
@CrossOrigin
public class PaketController {

	@Autowired
	private PaketRepo paketRepo;
	
	@GetMapping("/readPaket")
	public Iterable<Paket> getPakets(){
		return paketRepo.findAll();
	}
	@PostMapping
	public Paket addPakets(@RequestBody Paket paket) {
		Optional<Paket> findPaket = paketRepo.findByPaketName(paket.getPaketName());
		if (findPaket.toString() != "Optional.empty") {
			throw new RuntimeException("Sudah ada nama paket yang sama");
		}
		else {
			return paketRepo.save(paket);
		}
	}
}
