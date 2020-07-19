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
import com.pwd.pwdproject.util.EmailUtil;

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
	
	@Autowired
	private EmailUtil emailUtil;
	
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
	// post trx dari cart ke transaksi
	@PostMapping("/checkOut/{userId}")
	public Transaction checkOutToTransaction(@RequestBody Transaction transaction,@PathVariable int userId) {
		User findUser = userRepo.findById(userId).get();
		transaction.setUser(findUser);
		transaction.setStatusPengiriman("Belum Dikirim");
		return transactionRepo.save(transaction);
	}
	// upload bkt trf
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
	// download gambar
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
	// admin reject transaksi
	@PutMapping("/rejectTrf/{id}")
	public Transaction rejectTransaction(@PathVariable int id) {
		Transaction findTransaction = transactionRepo.findById(id).get();
		findTransaction.setStatus("Harap Upload Ulang Bukti Transfer, Karena gambar belum jelas");
		findTransaction.setBktTrf(null);
		return transactionRepo.save(findTransaction);
	}
	
	// admin acc transaksi
	int total2 = 9999;
	int total3 = 9999;
	int index = 1;
	String message = "";
	@PutMapping("/accTrf/{id}")
	public Transaction accTransaction(@RequestParam String tanggalSelesai,@PathVariable int id) {
		Transaction findTransaction = transactionRepo.findById(id).get(); // cari id trx
		findTransaction.setStatus("accepted");
		findTransaction.setStatusPengiriman("Sudah Dikirim");
		findTransaction.setTanggalSelesai(tanggalSelesai);
		total2 = 9999;
		total3 = 9999;
		index = 1;
		findTransaction.getTransactionDetails().forEach(val ->{ // get trx detail
			if (val.getPaket() == null) { // untuk product
				if (val.getProduct().getPaket() != null) { // product yg pya paket
					val.getProduct().setStockGudang(val.getProduct().getStockGudang() - val.getQuantity()); // yg awalnya stck user msl 3 jadi stock gdang jadi ikut 3
					val.getProduct().setSold(val.getProduct().getSold() + val.getQuantity()); // set sold + qty
					productRepo.save(val.getProduct());
					val.getProduct().getPaket().getProducts().forEach(value -> { // cari stock terendah utk disimpan stock ke paket
						if (total2 > value.getStock()) {
							total2 = value.getStock();
						}
						if (total3 > value.getStockGudang()) {
							total3 = value.getStockGudang();
						}
					});
					val.getProduct().getPaket().setStockPaket(total2); // set stock product yang punya paket
					val.getProduct().getPaket().setStockPaketGudang(total3);
					paketRepo.save(val.getProduct().getPaket());
				}
				else {
					val.getProduct().setStockGudang(val.getProduct().getStockGudang() - val.getQuantity()); // set stock gdang product yg ga punya paket
					val.getProduct().setSold(val.getProduct().getSold() + val.getQuantity()); // set sold product
					productRepo.save(val.getProduct());
				}
			}
			else {
				val.getPaket().setStockPaketGudang(val.getPaket().getStockPaketGudang() - val.getQuantity()); // set stock paket gudang - get qty
				val.getPaket().setSoldPaket(val.getPaket().getSoldPaket() + val.getQuantity()); // set sold paket ( get sold paket + get qty) 
				paketRepo.save(val.getPaket());
				val.getPaket().getProducts().forEach(value -> { // set stock gudang product yg ada d dlam paket
					value.setStockGudang(value.getStockGudang() - val.getQuantity()); // menjadi get stock gudang - get qty
				});
				productRepo.saveAll(val.getPaket().getProducts());
			}
		});
		message = "<h2>Hello "+findTransaction.getUser().getUsername()+",</h2>";
		message += "<img src=\"https://cdn.cnn.com/cnnnext/dam/assets/180926161922-gadget-logo-large-169.png\">";
		message += "<h3>Pembayaran Kamu Telah Berhasil dan Barangmu sudah dikirim</h3>";
		message += "<p>Terimakasih sudah berbelanja dan mendukung para penjual di Gadget Indonesia</p>";
		message += "<p>Status Pembayaran : "+findTransaction.getStatus()+"</p>";
		message += "<p>Jasa Pengiriman : "+findTransaction.getJasaPengiriman()+"</p>";
		message += "<p>Status Pengiriman : "+findTransaction.getStatusPengiriman()+"</p>";
		message += "<p>Tanggal Belanja : "+findTransaction.getTanggalBelanja()+"</p>";
		message += "<p>Tanggal Selesai : "+findTransaction.getTanggalSelesai()+"</p>";
		message += "<p>***********************DETAIL PRODUCT/PAKET*****************************</p>";
		findTransaction.getTransactionDetails().forEach(val ->{
			if (val.getPaket() == null) {
				message += "<p>"+index+". " + val.getProduct().getProductName() +" Rp. "+val.getPrice()+ 
				" ,Qty : "+val.getQuantity()+ " pcs. Total Harga : Rp. "+val.getTotalPrice()+". (Product)"+"</p>\n";
			}
			else {
				message += "<p>"+index+". " + val.getPaket().getPaketName() +" Rp. "+val.getPrice()+ 
				" ,Qty : "+val.getQuantity()+ " pcs. Total Harga : Rp. "+val.getTotalPrice()+". (Paket)"+"</p>";
			}
			index++;
		});
		message += "<p>***********************END OF PRODUCT/PAKET*****************************</p>";
		message += "<p>If this wasn't you,please ignore this email.</p>";
		message += "<p>Thanks,Gadget Indonesia</p>";
		emailUtil.sendEmail(findTransaction.getUser().getEmail(), "INVOICE PEMBELIAN FROM GADGET INDONESIA", message);
		return transactionRepo.save(findTransaction);	
	}
}
