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
	// add Cart
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
		findCart.setQuantity(findCart.getQuantity()+1);
		return cartRepo.save(findCart);
	}
	// find cart length di user id tsb
	@GetMapping("fillCart/{userId}")
	public Iterable<Cart> getCartByUser(@PathVariable int userId){
		return cartRepo.fillCart(userId);
	}
	// Delete Cart
	@DeleteMapping("/deleteCart/{cartId}")
	public void deleteCartById(@PathVariable int cartId) {
		cartRepo.deleteById(cartId);
	}
	
//	@PutMapping("/updateQty/{userId}")
//	public String updateQtyProductPaket(@PathVariable int userId) {
//		Iterable<Cart> findUserCart = cartRepo.fillCart(userId);
//		findUserCart.forEach(val -> {
//			if(val.getPaket() == null) {
//				Product findProduct = productRepo.findById(val.getProduct().getId()).get();
//				findProduct.setStock(findProduct.getStock() - val.getQuantity());
//				findProduct.getPaket().setStockPaket(0);
//				paketRepo.save(findProduct.getPaket());
//				findProduct.getPaket().getProducts().forEach(value->{
//					if(total2 > value.getStock()) {
//						total2 = value.getStock();
//					}
//				});
//				findProduct.getPaket().setStockPaket(total2);
//				paketRepo.save(findProduct.getPaket());
//				productRepo.save(findProduct);
//			}
//			else {
//				Paket findPaket = paketRepo.findById(val.getPaket().getId()).get();
//				findPaket.setStockPaket(findPaket.getStockPaket() - val.getQuantity());
//				findPaket.getProducts().forEach(value->{
//					value.setStock(value.getStock() - val.getQuantity());
//				});
//				productRepo.saveAll(findPaket.getProducts());
//				paketRepo.save(findPaket);
//			}
//		});
//		return "product dan paket sudah terbeli";
//	}
	
	
	
	@PutMapping("/update/{userId}")
	public String updateQtyProductPaket(@PathVariable int userId) {
		Iterable<Cart> findUserCart = cartRepo.fillCart(userId);
		findUserCart.forEach(val ->{
			if (val.getPaket()==null) {
				Product findProduct = productRepo.findById(val.getProduct().getId()).get();
				if (findProduct.getPaket() == null) {
					findProduct.setStock(findProduct.getStock() - val.getQuantity());
					productRepo.save(findProduct);
				}
				else {
					findProduct.setStock(findProduct.getStock() - val.getQuantity());
//					findProduct.getPaket().setStockPaket(0);
//					paketRepo.save(findProduct.getPaket());
					total2 = 9999;
					findProduct.getPaket().getProducts().forEach(value ->{
						if (total2 > value.getStock()) {
							total2 = value.getStock();
						}
					});
					findProduct.getPaket().setStockPaket(total2);
					paketRepo.save(findProduct.getPaket());
					productRepo.save(findProduct);				
				}
			}
			else {
				Paket findPaket = paketRepo.findById(val.getPaket().getId()).get();
				findPaket.setStockPaket(findPaket.getStockPaket() - val.getQuantity());
				findPaket.getProducts().forEach(value ->{
					value.setStock(value.getStock() - val.getQuantity());
				});
				productRepo.saveAll(findPaket.getProducts());
				paketRepo.save(findPaket);
			}
		});
		return "Stock Paket dan Product Berhasil Terubah";
	}
}