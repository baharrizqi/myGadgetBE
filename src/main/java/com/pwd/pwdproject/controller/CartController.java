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

import com.pwd.pwdproject.dao.CartRepo;
import com.pwd.pwdproject.dao.PaketRepo;
import com.pwd.pwdproject.dao.ProductRepo;
import com.pwd.pwdproject.dao.UserRepo;
import com.pwd.pwdproject.entity.Cart;
import com.pwd.pwdproject.entity.Paket;
import com.pwd.pwdproject.entity.Product;
import com.pwd.pwdproject.entity.User;

@RestController
@RequestMapping("/carts")
@CrossOrigin
public class CartController {

	@Autowired
	private CartRepo cartRepo;
	
	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private PaketRepo paketRepo;
	
	int total2 = 9999;
	
	// get semua cart
	@GetMapping("/readCart")
	public Iterable<Cart> getAllCart(){
		return cartRepo.findAll();
	}
	// get cart id
	@GetMapping("/readCart/{id}")
	public Optional<Cart> getCartById(@PathVariable int id) {
		return cartRepo.findById(id);
	}
	// klik add to Cart
	@PostMapping("/addCart/{userId}/{productId}/{paketId}")
	public Cart addToCart(@RequestBody Cart cart,@PathVariable int productId,@PathVariable int userId,@PathVariable int paketId) {
		User findUser = userRepo.findById(userId).get();
		if (paketId == 0 && productId != 0) {
			Product findProduct = productRepo.findById(productId).get();
			cart.setPaket(null);
			cart.setProduct(findProduct);
			cart.setUser(findUser);
			return cartRepo.save(cart);
		}
		else {
			Paket findPaket = paketRepo.findById(paketId).get();
			cart.setPaket(findPaket);
			cart.setProduct(null);
			cart.setUser(findUser);
			return cartRepo.save(cart);
		}
	}
	// get cart user dengan produk yg sama
	@GetMapping("/productCart/{userId}/{productId}")
	public Iterable<Cart> getCartUserProduct(@PathVariable int userId,@PathVariable int productId){
		return cartRepo.findProductinCart(userId, productId);
	}
	// get cart user dengan paket yg sama
	@GetMapping("/paketCart/{userId}/{paketId}")
	public Iterable<Cart> getCartUserPaket(@PathVariable int userId,@PathVariable int paketId){
		return cartRepo.findPaketinCart(userId, paketId);
	}
	// update cart/ bertambah qty kalo produk atau paketnya sama
	@PutMapping("/{cartId}")
	public Cart qtyCart(@PathVariable int cartId) {
		Cart findCart = cartRepo.findById(cartId).get();
		findCart.setQuantity(findCart.getQuantity() +1);
		return cartRepo.save(findCart);
	}
	// find cart length di user id tsb
	@GetMapping("fillCart/{userId}")
	public Iterable<Cart> getCartByUser(@PathVariable int userId){
		return cartRepo.fillCart(userId);
	}
	@DeleteMapping("/{cartId}")
	public void deleteCartById(@PathVariable int cartId) {
		cartRepo.deleteById(cartId);
	}
	// Delete Cart deleteCartQtyBackStock
	@DeleteMapping("/deleteCart/{cartId}")
	public void deleteCartQtyBackStock(@PathVariable int cartId) {
		Cart findCart = cartRepo.findById(cartId).get(); // cari id cart
		if (findCart.getPaket() == null) { // si cart ga pnya paket 
			findCart.getProduct().setStock(findCart.getProduct().getStock() + findCart.getQuantity()); // cart pnya prdct. set stock (get product dan stock di cart itu + qty)
			productRepo.save(findCart.getProduct());
			total2 = 99999;
			findCart.getProduct().getPaket().getProducts().forEach(val->{ // cart itu punya prdct dan paket , lalu paket pnya prdct get semua product utk cari stock terendah
				if (total2 > val.getStock()) {
					total2 = val.getStock();
				}
			});
			findCart.getProduct().getPaket().setStockPaket(total2); 
			paketRepo.save(findCart.getProduct().getPaket());
		}
		else {
			findCart.getPaket().setStockPaket(findCart.getPaket().getStockPaket() + findCart.getQuantity()); //cart itu punya paket set stockpaket(get paket stock pkt + qty)
			findCart.getPaket().getProducts().forEach(val->{ // cart punya paket dan product, lalu utk set stock product(get stock prdct + qty)
				val.setStock(val.getStock() + findCart.getQuantity());
			});
			productRepo.saveAll(findCart.getPaket().getProducts());
			paketRepo.save(findCart.getPaket());
		}
		cartRepo.deleteById(cartId);
	}
	// klik add to cart stock product/paket berkurang
	@PutMapping("/update/{productId}/{paketId}/{userId}")
	public String updateQtyProductPaket(@PathVariable int productId,@PathVariable int paketId,@PathVariable int userId) {
		if (paketId == 0 && productId != 0) {	 // masuk produk
					Product findProduct = productRepo.findById(productId).get(); // cari id product
				if (findProduct.getPaket() == null) { // product tdk punya paket
					findProduct.setStock(findProduct.getStock() - 1); // stock berkurang 1
					productRepo.save(findProduct);
				}
				else {
					findProduct.setStock(findProduct.getStock() - 1); // stock berkurang 1
					productRepo.save(findProduct);
					total2 = 9999;
					findProduct.getPaket().getProducts().forEach(val -> { // cari stock terendah di paket itu
						if (total2 > val.getStock()) {
							total2 = val.getStock();
						}
					});
					findProduct.getPaket().setStockPaket(total2);
					paketRepo.save(findProduct.getPaket());
				}	
		}
		else {
				Paket findPaket = paketRepo.findById(paketId).get();
				findPaket.setStockPaket(findPaket.getStockPaket() - 1); // beli paket, stock paket berkurang
				findPaket.getProducts().forEach(val->{ // product yg dalem paket stock -1 
					val.setStock(val.getStock() - 1);
				});
				productRepo.saveAll(findPaket.getProducts());
				paketRepo.save(findPaket);
		}
		return "Stock Paket dan Product Berhasil Terubah";
	}
		
}