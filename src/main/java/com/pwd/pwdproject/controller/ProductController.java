package com.pwd.pwdproject.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pwd.pwdproject.dao.CategoryRepo;
import com.pwd.pwdproject.dao.PaketRepo;
import com.pwd.pwdproject.dao.ProductRepo;
import com.pwd.pwdproject.entity.Category;
import com.pwd.pwdproject.entity.Paket;
import com.pwd.pwdproject.entity.Product;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {

	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
	private CategoryRepo categoryRepo;
	
	@Autowired
	private PaketRepo paketRepo;
	
	@GetMapping("/readProduct")
	public Iterable<Product> getAllProduct(){
		return productRepo.findAll();
	}
	@GetMapping("/readProduct/{id}")
	public Optional<Product> getProductById(@PathVariable int id) {
		return productRepo.findById(id);
	}
	
	@PostMapping
	public Product addProduct(@RequestBody Product product) {
		Optional<Product> findProduct = productRepo.findByProductName(product.getProductName());
		if (findProduct.toString() != "Optional.empty") {
			throw new RuntimeException("product name exists!");
		}
		return productRepo.save(product);
	}
	@PostMapping("/{productId}/category/{categoryId}")
	public Product AddCategoriesToProducts(@PathVariable int productId,@PathVariable int categoryId) {
		Product findProduct = productRepo.findById(productId).get();
		
		Category findCategory = categoryRepo.findById(categoryId).get();
		
		findProduct.getCategories().add(findCategory);
		
		return productRepo.save(findProduct);
	}
	@PutMapping("/{productId}")
	public Product editProduct(@RequestBody Product product,@PathVariable int productId) {
		Product findProduct = productRepo.findById(productId).get();
		product.setId(productId);
		product.setCategories(findProduct.getCategories());
		return productRepo.save(product);
	}
	
	// Delete Product
	@DeleteMapping("/delete/{id}")
	public void deleteProductById(@PathVariable int id) {
		Product findProduct = productRepo.findById(id).get();
		
		findProduct.getCategories().forEach(category -> {
			List<Product> categoryProduct = category.getProducts();
			categoryProduct.remove(findProduct);
			categoryRepo.save(category);
		});
		findProduct.setCategories(null);
		productRepo.deleteById(id);
	}
	
	@DeleteMapping("/delete/{productId}/category/{categoryId}")
	public Product deleteCategoryinProduct(@PathVariable int productId,@PathVariable int categoryId) {
		Product findProduct = productRepo.findById(productId).get();
		Category findCategory = categoryRepo.findById(categoryId).get();
		
		findProduct.getCategories().remove(findCategory);
		
		return productRepo.save(findProduct);
	}
	

	// Filter dan sort
	@GetMapping("/custom/{orderByType}/{orderByNamePrice}/{page}")
	public Iterable<Product> customQueryGet(@RequestParam double minPrice,@RequestParam double maxPrice,@RequestParam String namaProduk,@RequestParam String categoryName,@PathVariable String orderByType,@PathVariable String orderByNamePrice,@PathVariable int page){
		if(maxPrice == 0) {
			maxPrice = 99999999;
		}
		if (orderByType.equals("productName") && orderByNamePrice.equals("asc") ) {
			return productRepo.findProductByProductNameASC(minPrice,maxPrice,namaProduk,categoryName,page);
		}
		else if (orderByType.equals("productName") && orderByNamePrice.equals("desc")) {
			return productRepo.findProductByProductNameDESC(minPrice,maxPrice,namaProduk,categoryName,page);	
		}
		else if (orderByType.equals("price") && orderByNamePrice.equals("asc")) {
			return productRepo.findProductByPriceASC(minPrice,maxPrice,namaProduk,categoryName,page);
		}
		else if(orderByType.equals("price") && orderByNamePrice.equals("desc")) {
			return productRepo.findProductByPriceDESC(minPrice,maxPrice,namaProduk,categoryName,page);
		}
		else if(orderByType.equals("sold") && orderByNamePrice.equals("asc")) {
			return productRepo.findProductBySoldASC(minPrice,maxPrice,namaProduk,categoryName,page);
		}
		else {
			return productRepo.findProductBySoldDESC(minPrice,maxPrice,namaProduk,categoryName,page);
		}
	}
	
	// Count untuk Kategori Product
	@GetMapping("/countProduct")
	public int getCountProductCategory(@RequestParam double minPrice,@RequestParam double maxPrice,@RequestParam String namaProduk,@RequestParam String categoryName){
		return productRepo.getCountProduct(minPrice, maxPrice, namaProduk, categoryName);
	}
	// Count untuk ALL Product
	@GetMapping("/countProducts")
	public Iterable<Product> getCountProductCategoryAll(@RequestParam double minPrice,@RequestParam double maxPrice,@RequestParam String namaProduk,@RequestParam String categoryName){
		return productRepo.getCountProductAll(minPrice, maxPrice, namaProduk, categoryName);
	}
	
	@PostMapping("/pakets/{paketId}")
	public Product addPaketToProduct(@RequestBody Product product,@PathVariable int paketId) {
		Paket findPaket = paketRepo.findById(paketId).get();
		
		if(findPaket == null)
			throw new RuntimeException("Paket not found");
		
		product.setPaket(findPaket);
		
		return productRepo.save(product);
		
	}
	
	@PostMapping("{productId}/paket/{paketId}")
	public Product addProductToPaket(@PathVariable int productId, @PathVariable int paketId) {
		Product findProduct = productRepo.findById(productId).get();
		Paket findPaket = paketRepo.findById(paketId).get();
		
		if(findProduct.getPaket() == null) {
			findProduct.setPaket(findPaket);
			productRepo.save(findProduct);
			return findProduct;
		}
		else if (findProduct.getPaket() == findPaket) {
			throw new RuntimeException("Product sudah ada di paket lain");
		}
		findProduct.setPaket(findPaket);
		return productRepo.save(findProduct);
	}
	
	
	
	
//	@GetMapping("/category/{categoryId}")
//	public List<Product> getProductsOfCategory(@PathVariable int categoryId){
//		Category findCategory = categoryRepo.findById(categoryId).get();
//		return findCategory.getProducts();
//	}
}
