package beans;

import java.util.Date;

public class Order {
	private int id;
	private int user_id;
	private int seller_id;
	private Date date;
	private float total_price;

	public Date getDate() {
		return date;
	}

	public int getId() {
		return id;
	}

	public int getSeller_id() {
		return seller_id;
	}

	public float getTotal_price() {
		return total_price;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setSeller_id(int seller_id) {
		this.seller_id = seller_id;
	}

	public void setTotal_price(float total_price) {
		this.total_price = total_price;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public Order() {
	}

}
