package com.pwd.pwdproject.controller;

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

import com.pwd.pwdproject.dao.PaketRepo;
import com.pwd.pwdproject.dao.ProductRepo;
import com.pwd.pwdproject.entity.Category;
import com.pwd.pwdproject.entity.Paket;
import com.pwd.pwdproject.entity.Product;

@RestController
@RequestMapping("/paket")
@CrossOrigin
public class PaketController {

	@Autowired
	private PaketRepo paketRepo;
	
	@Autowired
	private ProductRepo productRepo;
	
	int total2 = 9999;
	
	@GetMapping("/readPaket")
	public Iterable<Paket> getPakets(){
		return paketRepo.findAll();
	}
	@GetMapping("/readPaket/{id}")
	public Optional<Paket> getPaketById(@PathVariable int id){
		return paketRepo.findById(id);
	}
	
	@PutMapping("/editPaket")
	public Paket editPaket(@RequestBody Paket paket) {
		System.out.println(paket.getPaketName() + paket.getImagePaket());
		Paket findPaket = paketRepo.findById(paket.getId()).get();
		findPaket.setPaketName(paket.getPaketName());
		findPaket.setImagePaket(paket.getImagePaket());
		return paketRepo.save(findPaket);
	}
	
	// add paket
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
	
	// delete produk di paket
	@DeleteMapping("/{paketId}/{productId}")
	public void deleteProductinPaket(@PathVariable int paketId,@PathVariable int productId) {
		Product findProduct = productRepo.findById(productId).get();
		Paket findPaket = paketRepo.findById(paketId).get();
		findPaket.setHargaPaket((double) (findPaket.getHargaPaket() - findProduct.getPrice())); // ngurangin price ke dari produk di paket,
		findProduct.setPaket(null); // set paket id jadi null
		total2 = 9999;	
		productRepo.save(findProduct); //save produk
		findPaket.getProducts().forEach(val ->{ // produk yg di dlm paket dicari stock terendah dgn foreach
			if(total2 > val.getStock()) {
				total2 = val.getStock();
			}	
		});
		findPaket.setStockPaket(total2);  // set stock paket dengan total stock yg di dapat sblmnya
		findPaket.setStockPaketGudang(total2);
		if (findPaket.getHargaPaket() == 0) { // jk paket yg didapatkan harganya == 0 maka set stock 0
			findPaket.setStockPaket(0); // set stock jadi 0 tidak jadi 9999
			findPaket.setStockPaketGudang(0);
		}
		paketRepo.save(findPaket);
	}
	
	// delete paket
	@DeleteMapping("/{paketId}")
	public String deletePaket(@PathVariable int paketId) {
		Paket findPaket = paketRepo.findById(paketId).get();
		if (findPaket.getStockPaket() != findPaket.getStockPaketGudang()) {
			throw new RuntimeException("Produk dalam paket tersebut masih dalam proses transaksi");
		}
		findPaket.getProducts().forEach(product -> {
			product.setPaket(null);
			productRepo.save(product);
		});
		
		findPaket.setProducts(null);
		
		paketRepo.deleteById(paketId);
		return "Berhasil hapus paket";
	}
	
	@GetMapping("/reportPaket/{orderBySold}")
	public Iterable<Paket> getReportPaket(@RequestParam double minPrice,@RequestParam double maxPrice,@RequestParam String namaPaket,@PathVariable String orderBySold) {
		if(maxPrice == 0) {
			maxPrice = 99999999;
		}
		if(orderBySold.equals("asc")) {
			return paketRepo.findReportByPaketASC(minPrice, maxPrice, namaPaket);
		}
		else {
			return paketRepo.findReportByPaketDESC(minPrice, maxPrice, namaPaket);
		}
	}
	
}
