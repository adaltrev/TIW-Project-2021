package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
		String query = "select o.id,o.shipping_date,o.total_price,s.name,o.seller_id from order as o join seller as s on o.seller_id=s.id where o.user_id=?";
		String containedProductQuery = "select p.name,s.price,c.amount from (contains as c join product as p on c.product_id=p.id) join sells as s on c.product_id=s.product_id where c.order_id=? and s.seller_id=?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // if there are no results, it returns null
					return null;
				while(result.next()) {
					List<CartProduct> cartProducts= new ArrayList<>();
					Order order=new Order();
					order.setId(result.getInt("id"));
					order.setDate(result.getDate("shipping_date"));
					order.setTotalPrice(result.getFloat("total_price"));
					order.setSeller(result.getString("name"));

					try (PreparedStatement pstatement1 = connection.prepareStatement(containedProductQuery);) {
						pstatement1.setInt(1, order.getId());
						pstatement1.setInt(1, result.getInt("seller_id"));
						try (ResultSet result1 = pstatement1.executeQuery();) {
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
				}
				return orders;
			}
		}
	}

}
