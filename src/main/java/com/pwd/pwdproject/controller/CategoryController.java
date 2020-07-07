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
import org.springframework.web.bind.annotation.RestController;

import com.pwd.pwdproject.dao.CategoryRepo;
import com.pwd.pwdproject.dao.ProductRepo;
import com.pwd.pwdproject.entity.Category;
import com.pwd.pwdproject.entity.Product;

@RestController
@RequestMapping("/category")
@CrossOrigin
public class CategoryController {

	@Autowired
	private CategoryRepo categoryRepo;
	
	@Autowired
	private ProductRepo productRepo;
	
	@GetMapping("/readCategory")
	public Iterable<Category> getAllCategory(){
		return categoryRepo.findAll();
	}
	@GetMapping("/readCategory/{id}")
	public Optional<Category> getCategoryById(@PathVariable int id) {
		return categoryRepo.findById(id);
	}
	@PostMapping
	public Category addCategory(@RequestBody Category category) {
		return categoryRepo.save(category);
	}
	
    @GetMapping("/{categoryId}/products")
    public List<Product> getProductsOfCategory(@PathVariable int categoryId){
    	Category findCategory = categoryRepo.findById(categoryId).get();
    	
    	return findCategory.getProducts();
    }
    
    // Edit Category
	@PutMapping("/editCategory")
	public Category editCategory(@RequestBody Category category) {
		Category findCategory = categoryRepo.findById(category.getId()).get();
		category.setProducts(findCategory.getProducts());
		return categoryRepo.save(category);
	}
	
	@DeleteMapping("/{categoryId}")
	public void deleteCategory(@PathVariable int categoryId) {
		Category findCategory = categoryRepo.findById(categoryId).get();
		
		findCategory.getProducts().forEach(product -> {
			List<Category> productCategory = product.getCategories();
			productCategory.remove(findCategory);
			productRepo.save(product);
		});
		
		findCategory.setProducts(null);
		
		categoryRepo.deleteById(categoryId);
	}
}
