package beans;

import java.util.Date;

public class Order {
	private int id;
	private int userId;
	private int sellerId;
	private Date date;
	private float totalPrice;
	private String shippingAddress;

	public Date getDate() {
		return date;
	}

	public int getId() {
		return id;
	}

	public int getSellerId() {
		return sellerId;
	}

	public float getTotalPrice() {
		return totalPrice;
	}

	public int getUserId() {
		return userId;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setSellerId(int sellerId) {
		this.sellerId = sellerId;
	}

	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Order() {
	}

}
