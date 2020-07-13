package com.pwd.pwdproject.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private double totalPrice;
	private String bktTrf;
	private String tanggalBelanja;
	private String tanggalSelesai;
	private String status;
	private String jasaPengiriman;
	private String statusPengiriman;
	
	@ManyToOne(cascade = {CascadeType.DETACH,CascadeType.PERSIST,CascadeType.REFRESH})
	@JoinColumn(name = "user_id")
	private User user;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "transaction",cascade = CascadeType.ALL)
	private List<TransactionDetail> transactionDetails;  
	

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public List<TransactionDetail> getTransactionDetails() {
		return transactionDetails;
	}

	public void setTransactionDetails(List<TransactionDetail> transactionDetails) {
		this.transactionDetails = transactionDetails;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getBktTrf() {
		return bktTrf;
	}

	public void setBktTrf(String bktTrf) {
		this.bktTrf = bktTrf;
	}

	public String getTanggalBelanja() {
		return tanggalBelanja;
	}

	public void setTanggalBelanja(String tanggalBelanja) {
		this.tanggalBelanja = tanggalBelanja;
	}

	public String getTanggalSelesai() {
		return tanggalSelesai;
	}

	public void setTanggalSelesai(String tanggalSelesai) {
		this.tanggalSelesai = tanggalSelesai;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getJasaPengiriman() {
		return jasaPengiriman;
	}

	public void setJasaPengiriman(String jasaPengiriman) {
		this.jasaPengiriman = jasaPengiriman;
	}

	public String getStatusPengiriman() {
		return statusPengiriman;
	}

	public void setStatusPengiriman(String statusPengiriman) {
		this.statusPengiriman = statusPengiriman;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	

}
