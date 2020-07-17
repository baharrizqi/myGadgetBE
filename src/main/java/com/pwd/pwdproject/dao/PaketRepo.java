package com.pwd.pwdproject.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import com.pwd.pwdproject.entity.Paket;
import com.pwd.pwdproject.entity.Product;

public interface PaketRepo extends JpaRepository<Paket, Integer> {
	public Optional<Paket> findByPaketName(String paketName);
	
	@Query(value = "SELECT * FROM paket where harga_paket >= :minPrice and harga_paket <= :maxPrice and paket_name like %:paketName% order by sold_paket asc",nativeQuery = true)
	public Iterable<Paket> findReportByPaketASC(@Param("minPrice") double minPrice,@Param("maxPrice") double maxPrice,@Param("paketName") String namaPaket);

	@Query(value = "SELECT * FROM paket where harga_paket >= :minPrice and harga_paket <= :maxPrice and paket_name like %:paketName% order by sold_paket desc",nativeQuery = true)
	public Iterable<Paket> findReportByPaketDESC(@Param("minPrice") double minPrice,@Param("maxPrice") double maxPrice,@Param("paketName") String namaPaket);
}
