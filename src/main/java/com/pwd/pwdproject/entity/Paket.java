package com.pwd.pwdproject.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Paket {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String paketName;
	private double hargaPaket;
	private String imagePaket;
	private int stockPaket;
	private int soldPaket;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paket",cascade = CascadeType.ALL)
//	@JsonIgnore
	private List<Product> products;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paket",cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Cart> carts;

	
		
	public List<Cart> getCarts() {
		return carts;
	}

	public void setCarts(List<Cart> carts) {
		this.carts = carts;
	}

	public String getImagePaket() {
		return imagePaket;
	}

	public void setImagePaket(String imagePaket) {
		this.imagePaket = imagePaket;
	}

	public int getSoldPaket() {
		return soldPaket;
	}

	public void setSoldPaket(int soldPaket) {
		this.soldPaket = soldPaket;
	}

	public int getStockPaket() {
		return stockPaket;
	}

	public void setStockPaket(int stockPaket) {
		this.stockPaket = stockPaket;
	}

	public double getHargaPaket() {
		return hargaPaket;
	}

	public void setHargaPaket(double hargaPaket) {
		this.hargaPaket = hargaPaket;
	}

	public String getPaketName() {
		return paketName;
	}

	public void setPaketName(String paketName) {
		this.paketName = paketName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	
}
