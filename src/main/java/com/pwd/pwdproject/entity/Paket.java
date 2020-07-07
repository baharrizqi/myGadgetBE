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
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "paket",cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Product> products;

	
	
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
