package com.pwd.pwdproject.controller;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pwd.pwdproject.dao.PaketRepo;
import com.pwd.pwdproject.dao.ProductRepo;
import com.pwd.pwdproject.dao.TransactionRepo;
import com.pwd.pwdproject.dao.UserRepo;
import com.pwd.pwdproject.entity.Cart;
import com.pwd.pwdproject.entity.Transaction;
import com.pwd.pwdproject.entity.User;

@RestController
@RequestMapping("/transaction")
@CrossOrigin
public class TransactionController {
	
	private String uploadPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";

	@Autowired
	private TransactionRepo transactionRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
	private PaketRepo paketRepo;
	
	@GetMapping("/readTransaction")
	public Iterable<Transaction> getAllTransaction(){
		return transactionRepo.findAll();
	}
	@GetMapping("/readTransaction/{id}")
	public Optional<Transaction> getTransactionById(@PathVariable int id) {
		return transactionRepo.findById(id);
	}
	@GetMapping("/fillTransaction/{userId}")
	public Iterable<Transaction> getTransactionByUserId(@PathVariable int userId) {
		return transactionRepo.fillTransaction(userId);
	}
	@PostMapping("/checkOut/{userId}")
	public Transaction checkOutToTransaction(@RequestBody Transaction transaction,@PathVariable int userId) {
		User findUser = userRepo.findById(userId).get();
		transaction.setUser(findUser);
		transaction.setStatusPengiriman("Belum Dikirim");
		return transactionRepo.save(transaction);
	}
	
	@PostMapping("/uploadBktTrf/{id}")
	public String uploadFile(@RequestParam("file") MultipartFile file,@PathVariable int id) throws JsonMappingException, JsonProcessingException {
		Transaction findTransaction = transactionRepo.findById(id).get();
		Date date = new Date();
		
//		Product product = new ObjectMapper().readValue(productString, Product.class );
//		System.out.println("BUKTI-TRF: "+ product.getProductName());
		
		String fileExtension = file.getContentType().split("/")[1];
		System.out.println(fileExtension);
		String newFileName = "BUKTI-TRF-" + date.getTime() + "." + fileExtension;
		
		String fileName = StringUtils.cleanPath(newFileName);
		
		Path path = Paths.get(StringUtils.cleanPath(uploadPath) + fileName);
		
		try {
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/transaction/download/").path(fileName).toUriString();
		
		findTransaction.setBktTrf(fileDownloadUri);
		findTransaction.setStatus("pending");
		transactionRepo.save(findTransaction);
		
		return fileDownloadUri;
	}
	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity<Object> downloadFile(@PathVariable String fileName){
		Path path = Paths.get(uploadPath + fileName);
		Resource resource = null;
		
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		System.out.println("DOWNLOAD");
		
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+ resource.getFilename()+ "\"").body(resource);
	}
	
	@PutMapping("/rejectTrf/{id}")
	public Transaction rejectTransaction(@PathVariable int id) {
		Transaction findTransaction = transactionRepo.findById(id).get();
		findTransaction.setStatus("Harap Upload Ulang Bukti Transfer, Karena gambar belum jelas");
		findTransaction.setBktTrf(null);
		return transactionRepo.save(findTransaction);
	}
	int total2 = 9999;
	@PutMapping("/accTrf/{id}")
	public Transaction accTransaction(@RequestParam String tanggalSelesai,@PathVariable int id) {
		Transaction findTransaction = transactionRepo.findById(id).get();
		findTransaction.setStatus("accepted");
		findTransaction.setStatusPengiriman("Sudah Dikirim");
		total2 = 9999;
		findTransaction.getTransactionDetails().forEach(val ->{
			if (val.getPaket() == null) {
				if (val.getProduct().getPaket() != null) {
					val.getProduct().setStockGudang(val.getProduct().getStock());
					val.getProduct().setSold(val.getProduct().getSold() + val.getQuantity());
					productRepo.save(val.getProduct());
					val.getProduct().getPaket().getProducts().forEach(value -> {
						if (total2 > value.getStock()) {
							total2 = value.getStock();
						}
					});
					val.getProduct().getPaket().setStockPaket(total2);
					val.getProduct().getPaket().setStockPaketGudang(total2);
					paketRepo.save(val.getProduct().getPaket());
				}
				else {
					val.getProduct().setStockGudang(val.getProduct().getStock());
					val.getProduct().setSold(val.getProduct().getSold() + val.getQuantity());
					productRepo.save(val.getProduct());
				}
			}
			else {
				val.getPaket().setStockPaketGudang(val.getPaket().getStockPaket());
				val.getPaket().setSoldPaket(val.getPaket().getSoldPaket() + val.getQuantity());
				paketRepo.save(val.getPaket());
				val.getPaket().getProducts().forEach(value -> {
					value.setStockGudang(value.getStockGudang() - val.getQuantity());
				});
				productRepo.saveAll(val.getPaket().getProducts());
			}
		});
		return transactionRepo.save(findTransaction);	
	}
}
