package com.pwd.pwdproject.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String productName;
	private String merek;
	private double price;
	private String year;
	private int stock;
	private String image;
	private String description;
	private int sold;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.PERSIST,
	        CascadeType.REFRESH })
	@JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
//	@JsonIgnore
	private List<Category> categories;
	

	
	@ManyToOne(cascade = {CascadeType.DETACH,CascadeType.PERSIST,CascadeType.REFRESH})
	@JoinColumn(name = "paket_id")
	@JsonIgnore
	private Paket paket;
	
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product",cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Cart> carts;
	
	
	
	public List<Cart> getCarts() {
		return carts;
	}
	public void setCarts(List<Cart> carts) {
		this.carts = carts;
	}
	public Paket getPaket() {
		return paket;
	}
	public void setPaket(Paket paket) {
		this.paket = paket;
	}
	public int getSold() {
		return sold;
	}
	public void setSold(int sold) {
		this.sold = sold;
	}
	public List<Category> getCategories() {
		return categories;
	}
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	public String getMerek() {
		return merek;
	}
	public void setMerek(String merek) {
		this.merek = merek;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	
}
