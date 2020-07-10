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
	
	double total = 0;
	int total2 = 9999;
	
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
	
	// edit product
	@PutMapping("/{productId}")
	public Product editProduct(@RequestBody Product product,@PathVariable int productId) {
		Product findProduct = productRepo.findById(productId).get();
		product.setId(productId);
		product.setCategories(findProduct.getCategories());
		product.setPaket(findProduct.getPaket());
		total2=9999;
		if (findProduct.getPaket() != null) {			
//			System.out.println(findProduct.getPrice());
//			System.out.println(findProduct.getPaket().getHargaPaket());
//			System.out.println(product.getPrice());
			findProduct.getPaket().setHargaPaket(findProduct.getPaket().getHargaPaket() - findProduct.getPrice() + product.getPrice());
			productRepo.save(product);
			findProduct.setStock(product.getStock());
			findProduct.getPaket().setStockPaket(0);
			System.out.println(findProduct.getPaket().getStockPaket());
			paketRepo.save(findProduct.getPaket());
			findProduct.getPaket().getProducts().forEach(val ->{
				if (total2 > val.getStock()) {
					total2 = val.getStock();
				}
			});
			findProduct.getPaket().setStockPaket(total2);
			paketRepo.save(findProduct.getPaket());
		}
		
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
		findProduct.setPaket(null);
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
	public Iterable<Product> customQueryGet(@RequestParam double minPrice,@RequestParam double maxPrice,@RequestParam String namaProduk,@RequestParam String merek,@RequestParam String categoryName,@PathVariable String orderByType,@PathVariable String orderByNamePrice,@PathVariable int page){
		if(maxPrice == 0) {
			maxPrice = 99999999;
		}
		if (orderByType.equals("productName") && orderByNamePrice.equals("asc") ) {
			return productRepo.findProductByProductNameASC(minPrice,maxPrice,namaProduk,merek,categoryName,page);
		}
		else if (orderByType.equals("productName") && orderByNamePrice.equals("desc")) {
			return productRepo.findProductByProductNameDESC(minPrice,maxPrice,namaProduk,merek,categoryName,page);	
		}
		else if (orderByType.equals("price") && orderByNamePrice.equals("asc")) {
			return productRepo.findProductByPriceASC(minPrice,maxPrice,namaProduk,merek,categoryName,page);
		}
		else if(orderByType.equals("price") && orderByNamePrice.equals("desc")) {
			return productRepo.findProductByPriceDESC(minPrice,maxPrice,namaProduk,merek,categoryName,page);
		}
		else if(orderByType.equals("sold") && orderByNamePrice.equals("asc")) {
			return productRepo.findProductBySoldASC(minPrice,maxPrice,namaProduk,merek,categoryName,page);
		}
		else {
			return productRepo.findProductBySoldDESC(minPrice,maxPrice,namaProduk,merek,categoryName,page);
		}
	}
	
	// Count untuk Kategori Product
	@GetMapping("/countProduct")
	public int getCountProductCategory(@RequestParam double minPrice,@RequestParam double maxPrice,@RequestParam String namaProduk,@RequestParam String merek,@RequestParam String categoryName){
		return productRepo.getCountProduct(minPrice, maxPrice, namaProduk,merek, categoryName);
	}
	// Count untuk ALL Product
	@GetMapping("/countProducts")
	public Iterable<Product> getCountProductCategoryAll(@RequestParam double minPrice,@RequestParam double maxPrice,@RequestParam String namaProduk,@RequestParam String merek,@RequestParam String categoryName){
		return productRepo.getCountProductAll(minPrice, maxPrice, namaProduk,merek, categoryName);
	}
	
	// add product to paket
	@PostMapping("{productId}/paket/{paketId}")
	public Product addProductToPaket(@PathVariable int productId, @PathVariable int paketId) {
		Product findProduct = productRepo.findById(productId).get();
		Paket findPaket = paketRepo.findById(paketId).get();
		total = 0;
		total2 = 9999;
		findPaket.setHargaPaket(0);
		paketRepo.save(findPaket);
		if(findProduct.getPaket() == null) {
			findProduct.setPaket(findPaket);
			productRepo.save(findProduct);
			findPaket.getProducts().forEach(product ->{
				if(total2 > product.getStock()) {
					total2 = product.getStock();
				}
				total += product.getPrice();
			});
			findPaket.setHargaPaket(total);
			findPaket.setStockPaket(total2);
			paketRepo.save(findPaket);
			return findProduct;
		}
		else if (findProduct.getPaket() == findPaket) {
			throw new RuntimeException("Product sudah ada di paket yang sama");
		}
		findProduct.getPaket().setHargaPaket(findProduct.getPaket().getHargaPaket()-findProduct.getPrice()); // ngurangin price ke dari produk ke paket, produk udah pindah
		int cariIdProduct = findProduct.getPaket().getId(); // cari id di paket bossku
		findProduct.setPaket(null); // set paket id == null
		productRepo.save(findProduct); // kemudian di save
		total2 = 9999;
		Paket findPaketEditStockPaket = paketRepo.findById(cariIdProduct).get(); // cari id paket yg bekas produk
		findPaketEditStockPaket.getProducts().forEach(product->{ // cari stok terendah bekas produk di paket dgn perulangan
			if(total2 > product.getStock()) {
				total2 = product.getStock();
			}
//			total += product.getPrice();
		});
		findPaketEditStockPaket.setStockPaket(total2); // id paket yg bekas produk di set stock menjadi 0 / mis 10
		if(findPaketEditStockPaket.getHargaPaket() == 0) { // utk pencegahan logika sblm yg di atas harus pake bosku
			findPaketEditStockPaket.setStockPaket(0);
		}
		paketRepo.save(findPaketEditStockPaket); // simpan paket bekas produk 
		findProduct.setPaket(findPaket); // produk setpaket jadi find paket utk paket yg baru
		paketRepo.save(findPaket); // save paket baru
		total2 = 9999;
		findPaket.getProducts().forEach(product -> { // cari produk di dlm paket dgn perulangan untuk cari stock terendah
			if (total2 > product.getStock()) {
				total2 = product.getStock();
			}
			total += product.getPrice(); // itung price utk paket yg baru
		});
		findPaket.setHargaPaket(total); // set harga paket 
		findPaket.setStockPaket(total2); // set stock
		paketRepo.save(findPaket);
		return findProduct;
	}
}
