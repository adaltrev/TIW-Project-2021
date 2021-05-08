package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import beans.CartProduct;
import beans.Order;



public class OrderDao {
	private Connection connection;

	public OrderDao(Connection connection) {
		this.connection = connection;
	}
	
	public List<Order> findOrdersbyUserId(int userId) throws SQLException{
		List<Order> orders= new ArrayList<>();
		String query = "select o.id,o.shipping_date,o.total_price,s.name,o.seller_id,u.address from (orders as o join seller as s on o.seller_id=s.id) join user as u on o.user_id=u.id where o.user_id=? order by o.shipping_date DESC";
		String containedProductQuery = "select p.name,s.price,c.amount from (contain as c join product as p on c.product_id=p.id) join sells as s on c.product_id=s.product_id where c.order_id=? and s.seller_id=?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // if there are no results, it returns null
					return null;
				while(result.next()) {
					Order order=new Order();
					order.setId(result.getInt("id"));
					order.setDate(result.getDate("shipping_date"));
					order.setTotalPrice(result.getFloat("total_price"));
					order.setSeller(result.getString("name"));
					order.setShippingAddress(result.getString("address"));

					try (PreparedStatement pstatement1 = connection.prepareStatement(containedProductQuery);) {
						pstatement1.setInt(1, order.getId());
						pstatement1.setInt(2, result.getInt("seller_id"));
						try (ResultSet result1 = pstatement1.executeQuery();) {
							List<CartProduct> cartProducts= new ArrayList<>();
							if (!result1.isBeforeFirst())
								return null;

							while (result1.next()) {
								CartProduct cartProduct= new CartProduct();
								cartProduct.setName(result1.getString("name"));
								cartProduct.setPrice(result1.getFloat("price"));
								cartProduct.setAmount(result1.getInt("amount"));
								cartProducts.add(cartProduct);
							}
							
							order.setProducts(cartProducts);
						}
					}
					orders.add(order);
				}
				return orders;
			}
		}
	}
	
	
	public Integer findLastOrder(int Uid) throws SQLException{
		String query="select max(id) from orders where user_id = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, Uid);
			
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // if there are no results, it returns null
					throw new SQLException();
				else {
					result.next();
					return result.getInt("id");
				}
			}
		} 
	}
	
	
	public void createOrder(int Uid, int Sid, List<CartProduct> products) throws SQLException {
		String orders="insert into orders(id,user_id,seller_id,shipping_date,total_price) values(NULL,?,?,?,?)";
		String contain="insert into contain(order_id,user_id,product_id,amount) values(?,?,?,?)";
		int add;
		int totalProducts;
		float productPrice;
		float freeShipping;
		
		try (PreparedStatement pstatement = connection.prepareStatement(orders);) {
			pstatement.setInt(1, Uid);
			pstatement.setInt(2, Sid);
			
			//Get current date, create random offset (1-7 days) and set SQL date
			java.util.Date date = new java.util.Date();
			Calendar c = Calendar.getInstance();
	        c.setTime(date);
	        add = (int) (Math.random() * 6) + 1;
	        c.add(Calendar.DATE, add);
	        date=c.getTime();
			java.sql.Date mySqlDate = new java.sql.Date(date.getTime());
			pstatement.setDate(3, mySqlDate);
			
			//Set SQL total_price, adding total price of products and shipping price
			SellerDao sellerDao = new SellerDao(connection);
			totalProducts=0;
			productPrice=0;
			for(CartProduct cp : products) {
				productPrice =+ (cp.getAmount()*cp.getPrice());
				totalProducts =+ cp.getAmount();
			}
			freeShipping = sellerDao.findSellerById(Sid).getFreeShipping();
			if(productPrice>=freeShipping) {
				pstatement.setFloat(4, productPrice);
			}
			else {
				pstatement.setFloat(4, productPrice + sellerDao.findShippingPrice(Sid, totalProducts));
			}
			
			//Insert Order entry into database
			pstatement.executeUpdate();
			
			//Retrieve current order id from database
			int id = this.findLastOrder(Uid);
			
			//Insert all Contain entries into database
			try ( PreparedStatement pstatement1 = connection.prepareStatement(contain);){
				for(CartProduct cp : products) {
					pstatement.setInt(1, id); 
					pstatement.setInt(2, Uid);
					pstatement.setInt(3, cp.getId());
					pstatement.setInt(4, cp.getAmount());
					pstatement.executeUpdate();
				}
			}
		}
	}

}
