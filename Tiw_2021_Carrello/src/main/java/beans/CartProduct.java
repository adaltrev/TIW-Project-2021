package beans;

public class CartProduct {
	private int id;
	private int amount;
	private float price;
	
	public int getAmount() {
		return amount;
	}
	
	public int getId() {
		return id;
	}
	
	public float getPrice() {
		return price;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setPrice(float price) {
		this.price = price;
	}
}
