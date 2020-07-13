package com.pwd.pwdproject.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pwd.pwdproject.dao.PaketRepo;
import com.pwd.pwdproject.dao.ProductRepo;
import com.pwd.pwdproject.dao.TransactionDetailRepo;
import com.pwd.pwdproject.dao.TransactionRepo;
import com.pwd.pwdproject.entity.Paket;
import com.pwd.pwdproject.entity.Product;
import com.pwd.pwdproject.entity.Transaction;
import com.pwd.pwdproject.entity.TransactionDetail;

@RestController
@RequestMapping("/transactionDetail")
@CrossOrigin
public class TransactionDetailController {

	@Autowired
	private TransactionDetailRepo transactionDetailRepo;
	
	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
	private TransactionRepo transactionRepo;
	
	@Autowired
	private PaketRepo paketRepo;
	
	@GetMapping("/readTransactionDetail")
	public Iterable<TransactionDetail> getAllTransactionDetail(){
		return transactionDetailRepo.findAll();
	}
	@GetMapping("/readTransactionDetail/{id}")
	public Optional<TransactionDetail> getTransactionDetailById(@PathVariable int id){
		return transactionDetailRepo.findById(id);
	}
	@PostMapping("/checkOutTransactionDetail/{transactionId}/{productId}/{paketId}")
	public TransactionDetail checkOutTransactionDetail(@RequestBody TransactionDetail transactionDetail,@PathVariable int transactionId,@PathVariable int productId,@PathVariable int paketId) {
		if(paketId == 0 && productId != 0) {
			Transaction findTransaction = transactionRepo.findById(transactionId).get();
			Product findProduct = productRepo.findById(productId).get();
			transactionDetail.setProduct(findProduct);
			transactionDetail.setPaket(null);
			transactionDetail.setTransaction(findTransaction);
			return transactionDetailRepo.save(transactionDetail);
		}
		else {
			Transaction findTransaction = transactionRepo.findById(transactionId).get();
			System.out.println(paketId);
			Paket findPaket = paketRepo.findById(paketId).get();
			transactionDetail.setProduct(null);
			transactionDetail.setPaket(findPaket);
			transactionDetail.setTransaction(findTransaction);
			return transactionDetailRepo.save(transactionDetail);	
		}
	}
}
