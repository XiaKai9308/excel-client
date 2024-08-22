package cn.tamilin.api.client;

import java.math.BigDecimal;

public class Product {

	private String id;

	private String code;

	private String oe;

	private String name;

	private String brand;

	private String type;

	private String model;

	private int quantity;

	private BigDecimal price;

	private int last_update;

	private int success_time;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOe() {
		return oe;
	}

	public void setOe(String oe) {
		this.oe = oe;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getLast_update() {
		return last_update;
	}

	public void setLast_update(int last_update) {
		this.last_update = last_update;
	}

	public int getSuccess_time() {
		return success_time;
	}

	public void setSuccess_time(int success_time) {
		this.success_time = success_time;
	}

	@Override
	public String toString() {
		return String.format("Product [id=%s, code=%s, oe=%s, name=%s, brand=%s, type=%s, model=%s, quantity=%s, price=%s, last_update=%s, success_time=%s]", id, code, oe, name, brand, type, model, quantity, price, last_update, success_time);
	}
}
