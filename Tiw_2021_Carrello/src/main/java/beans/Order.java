package beans;

import java.util.Date;

public class Order {
	private int id;
	private String seller;
	private Date date;
	private float totalPrice;
	private String shippingAddress;

	public Date getDate() {
		return date;
	}

	public int getId() {
		return id;
	}

	public String getSeller() {
		return seller;
	}

	public float getTotalPrice() {
		return totalPrice;
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

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}


	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Order() {
	}

}
