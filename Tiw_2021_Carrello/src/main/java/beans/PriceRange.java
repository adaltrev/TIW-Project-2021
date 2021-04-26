package beans;

public class PriceRange {

	private int id;
	private int min;
	private int max;
	private float price;
	
	public int getId() {
		return id;
	}
	
	public int getMax() {
		return max;
	}
	
	public int getMin() {
		return min;
	}
	
	public float getPrice() {
		return price;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setMax(int max) {
		this.max = max;
	}
	
	public void setMin(int min) {
		this.min = min;
	}
	
	public void setPrice(float price) {
		this.price = price;
	}
}
