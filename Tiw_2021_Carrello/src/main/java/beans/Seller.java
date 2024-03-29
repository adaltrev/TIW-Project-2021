package beans;

import java.util.List;

public class Seller {

	private int id;
	private String name;
	private float rating;
	private float freeShipping;
	private float productPrice;
	private List<PriceRange> ranges;
	private float cartPrice;
	private float shippingPrice;

	public Seller() {
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public float getRating() {
		return rating;
	}

	public float getFreeShipping() {
		return freeShipping;
	}

	public float getProductPrice() {
		return productPrice;
	}

	public List<PriceRange> getRanges() {
		return ranges;
	}

	public float getCartPrice() {
		return cartPrice;
	}

	public float getShippingPrice() {
		return shippingPrice;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public void setFreeShipping(float freeShipping) {
		this.freeShipping = freeShipping;
	}

	public void setProductPrice(float productPrice) {
		this.productPrice = productPrice;
	}

	public void setRanges(List<PriceRange> ranges) {
		this.ranges = ranges;
	}

	public void setCartPrice(float cartPrice) {
		this.cartPrice = cartPrice;
	}

	public void setShippingPrice(float shippingPrice) {
		this.shippingPrice = shippingPrice;
	}
}
