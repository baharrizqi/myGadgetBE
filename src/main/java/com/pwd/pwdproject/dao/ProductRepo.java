package com.pwd.pwdproject.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.pwd.pwdproject.entity.Product;

public interface ProductRepo extends JpaRepository<Product, Integer> {
	public Optional<Product> findByProductName(String productName);
	
	
//	@Query(value = "SELECT * FROM product_category pc join product p on pc.product_id = p.id join category c on pc.category_id = c.id where price >= :minPrice and price <= :maxPrice and product_name like %:productName% and c.category_name like %:categoryName% group by product_name",nativeQuery = true)
//	public Iterable<Product> findProductByPrice(@Param("minPrice") double minPrice,@Param("maxPrice") double maxPrice,@Param("productName") String namaProduk,@Param("categoryName") String categoryName);
	
	@Query(value = "SELECT * FROM product_category pc join product p on pc.product_id = p.id join category c on pc.category_id = c.id where price >= :minPrice and price <= :maxPrice and product_name like %:productName% and merek like %:merek% and c.category_name like %:categoryName% group by product_name order by product_name asc limit 4 offset :page",nativeQuery = true)
	public Iterable<Product> findProductByProductNameASC(@Param("minPrice") double minPrice,@Param("maxPrice") double maxPrice,@Param("productName") String namaProduk,@RequestParam String merek,@Param("categoryName") String categoryName,@PathVariable int page);

	@Query(value = "SELECT * FROM product_category pc join product p on pc.product_id = p.id join category c on pc.category_id = c.id where price >= :minPrice and price <= :maxPrice and product_name like %:productName% and merek like %:merek% and c.category_name like %:categoryName% group by product_name order by product_name desc limit 4 offset :page",nativeQuery = true)
	public Iterable<Product> findProductByProductNameDESC(@Param("minPrice") double minPrice,@Param("maxPrice") double maxPrice,@Param("productName") String namaProduk,@RequestParam String merek,@Param("categoryName") String categoryName,@PathVariable int page);

	@Query(value = "SELECT * FROM product_category pc join product p on pc.product_id = p.id join category c on pc.category_id = c.id where price >= :minPrice and price <= :maxPrice and product_name like %:productName% and merek like %:merek% and c.category_name like %:categoryName% group by product_name order by price asc limit 4 offset :page",nativeQuery = true)
	public Iterable<Product> findProductByPriceASC(@Param("minPrice") double minPrice,@Param("maxPrice") double maxPrice,@Param("productName") String namaProduk,@RequestParam String merek,@Param("categoryName") String categoryName,@PathVariable int page);

	@Query(value = "SELECT * FROM product_category pc join product p on pc.product_id = p.id join category c on pc.category_id = c.id where price >= :minPrice and price <= :maxPrice and product_name like %:productName% and merek like %:merek% and c.category_name like %:categoryName% group by product_name order by price desc limit 4 offset :page",nativeQuery = true)
	public Iterable<Product> findProductByPriceDESC(@Param("minPrice") double minPrice,@Param("maxPrice") double maxPrice,@Param("productName") String namaProduk,@RequestParam String merek,@Param("categoryName") String categoryName,@PathVariable int page);

	@Query(value = "SELECT * FROM product_category pc join product p on pc.product_id = p.id join category c on pc.category_id = c.id where price >= :minPrice and price <= :maxPrice and product_name like %:productName% and merek like %:merek% and c.category_name like %:categoryName% group by product_name order by sold asc limit 4 offset :page",nativeQuery = true)
	public Iterable<Product> findProductBySoldASC(@Param("minPrice") double minPrice,@Param("maxPrice") double maxPrice,@Param("productName") String namaProduk,@RequestParam String merek,@Param("categoryName") String categoryName,@PathVariable int page);
	
	@Query(value = "SELECT * FROM product_category pc join product p on pc.product_id = p.id join category c on pc.category_id = c.id where price >= :minPrice and price <= :maxPrice and product_name like %:productName% and merek like %:merek% and c.category_name like %:categoryName% group by product_name order by sold desc limit 4 offset :page",nativeQuery = true)
	public Iterable<Product> findProductBySoldDESC(@Param("minPrice") double minPrice,@Param("maxPrice") double maxPrice,@Param("productName") String namaProduk,@RequestParam String merek,@Param("categoryName") String categoryName,@PathVariable int page);
	
	
	@Query(value = "SELECT * FROM product_category pc join product p on pc.product_id = p.id join category c on pc.category_id = c.id where price >= :minPrice and price <= :maxPrice and product_name like %:productName% and merek like %:merek% and c.category_name like %:categoryName% group by product_name",nativeQuery = true)
	public Iterable<Product> getCountProductAll(@Param("minPrice") double minPrice,@Param("maxPrice") double maxPrice,@Param("productName") String namaProduk,@RequestParam String merek,@Param("categoryName") String categoryName);
	
	@Query(value = "SELECT count(*) FROM product_category pc join product p on pc.product_id = p.id join category c on pc.category_id = c.id where price >= :minPrice and price <= :maxPrice and product_name like %:productName% and merek like %:merek% and c.category_name like %:categoryName%",nativeQuery = true)
	public int getCountProduct(@Param("minPrice") double minPrice,@Param("maxPrice") double maxPrice,@Param("productName") String namaProduk,@RequestParam String merek,@Param("categoryName") String categoryName);
	

	@Query(value = "SELECT * FROM product_category pc join product p on pc.product_id = p.id join category c on pc.category_id = c.id where price >= :minPrice and price <= :maxPrice and product_name like %:productName% and merek like %:merek% and c.category_name like %:categoryName% group by product_name order by sold asc",nativeQuery = true)
	public Iterable<Product> findReportByProductASC(@Param("minPrice") double minPrice,@Param("maxPrice") double maxPrice,@Param("productName") String namaProduk,@RequestParam String merek,@Param("categoryName") String categoryName);
	
	@Query(value = "SELECT * FROM product_category pc join product p on pc.product_id = p.id join category c on pc.category_id = c.id where price >= :minPrice and price <= :maxPrice and product_name like %:productName% and merek like %:merek% and c.category_name like %:categoryName% group by product_name order by sold desc",nativeQuery = true)
	public Iterable<Product> findReportByProductDESC(@Param("minPrice") double minPrice,@Param("maxPrice") double maxPrice,@Param("productName") String namaProduk,@RequestParam String merek,@Param("categoryName") String categoryName);

	@Query(value = "SELECT * FROM product order by sold desc limit 5",nativeQuery = true)
	public Iterable<Product> findReportByProductHomeLaris();
}
