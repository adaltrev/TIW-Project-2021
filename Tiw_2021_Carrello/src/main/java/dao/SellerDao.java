
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import beans.PriceRange;
import beans.Seller;

public class SellerDao {

	private Connection connection;

	public SellerDao(Connection connection) {
		this.connection = connection;
	}
	
	public Seller findSellerById(int id) throws SQLException {
		String query = "SELECT  * FROM seller  WHERE id = ?";
		String rangeQuery = "SELECT r.max, r.min, r.price FROM price_range AS r JOIN seller AS s ON r.seller_id=s.id WHERE s.id = ?";
		Seller seller;
		List<PriceRange> ranges;
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // if there are no results, it returns null
					return null;
				else {
					result.next();
					ranges = new ArrayList<>();
					seller= new Seller();
					seller.setId(result.getInt("id"));
					seller.setName(result.getString("name"));
					seller.setRating(result.getFloat("rating"));
					seller.setFreeShipping(result.getFloat("free_shipping"));
					seller.setProductPrice(result.getFloat("price"));
					
					try(PreparedStatement pstatement1 = connection.prepareStatement(rangeQuery);){
						pstatement1.setInt(1, seller.getId());
						try(ResultSet result1 = pstatement1.executeQuery();){
							if(!result1.isBeforeFirst())
								return null;
							
							while(result1.next()) {
								PriceRange range= new PriceRange();
								range.setMin(result1.getInt("min"));
								range.setMax(result1.getInt("max"));
								range.setPrice(result1.getFloat("price"));
								ranges.add(range);
							}
							
							seller.setRanges(ranges);
						}
					}
					return seller;
				}
			}
		}
	}
	
	
	public List<Seller> findSellersByProduct(int id) throws SQLException{
		String sellerQuery = "SELECT s.id, s.name, s.rating, s.free_shipping, sl.price FROM seller AS s JOIN sells AS sl ON s.id=sl.seller_id WHERE sl.product_id = ?";
		String rangeQuery = "SELECT r.max, r.min, r.price FROM price_range AS r JOIN seller AS s ON r.seller_id=s.id WHERE s.id = ?";
		List<Seller> sellers=new ArrayList<>();
		List<PriceRange> ranges;

		try(PreparedStatement pstatement = connection.prepareStatement(sellerQuery);){
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // If there are no results, it returns null
					return null;
				
				 while (result.next()){
					ranges = new ArrayList<>();
					Seller seller= new Seller();
					seller.setId(result.getInt("id"));
					seller.setName(result.getString("name"));
					seller.setRating(result.getFloat("rating"));
					seller.setFreeShipping(result.getFloat("free_shipping"));
					seller.setProductPrice(result.getFloat("price"));
					
					try(PreparedStatement pstatement1 = connection.prepareStatement(rangeQuery);){
						pstatement1.setInt(1, seller.getId());
						try(ResultSet result1 = pstatement1.executeQuery();){
							if(!result1.isBeforeFirst())
								break;
							
							while(result1.next()) {
								PriceRange range= new PriceRange();
								range.setMin(result1.getInt("min"));
								range.setMax(result1.getInt("max"));
								range.setPrice(result1.getFloat("price"));
								ranges.add(range);
							}
							
							seller.setRanges(ranges);
						}
					}
					
					sellers.add(seller);
				}
			}
		}
		
		return sellers;
	}
	
	
	public Integer checkSell(int Pid, int Sid) throws SQLException {
		String query="SELECT price FROM sells WHERE product_id = ? AND seller_id = ?";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			pstatement.setInt(1, Pid);
			pstatement.setInt(2, Sid);
			
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return null;
				else
					return result.getInt("price");
			}
		}
	}
	
}
