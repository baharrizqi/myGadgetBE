package com.pwd.pwdproject.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import com.pwd.pwdproject.entity.Cart;

public interface CartRepo extends JpaRepository<Cart, Integer> {

	@Query(value = "SELECT * FROM cart where user_id= :userId and product_id= :productId",nativeQuery = true)
	public Iterable<Cart> findProductinCart(@PathVariable int userId,@PathVariable int productId);
	
	@Query(value = "SELECT * FROM cart where user_id= :userId and paket_id= :paketId",nativeQuery = true)
	public Iterable<Cart> findPaketinCart(@PathVariable int userId,@PathVariable int paketId);
	
	@Query(value = "SELECT * FROM cart where user_id= :userId",nativeQuery = true)
	public Iterable<Cart> fillCart(@PathVariable int userId);
	
	@Query(value = "SELECT * FROM cart where product_id= :productId and user_id= :userId",nativeQuery = true)
	public Cart findByProductId(@PathVariable int productId,@PathVariable int userId);
	
	@Query(value = "SELECT * FROM cart where paket_id= :paketId and user_id= :userId",nativeQuery = true)
	public Cart findByPaketId(@PathVariable int paketId,@PathVariable int userId);
}
