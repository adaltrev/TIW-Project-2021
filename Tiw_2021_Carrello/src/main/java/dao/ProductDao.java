package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.Product;

public class ProductDao {
	private Connection connection;

	public ProductDao(Connection connection) {
		this.connection = connection;
	}
	
	public Product findProductById(int id) throws SQLException {
		String query = "SELECT  * FROM product  WHERE id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // if there are no results, it returns null
					return null;
				else {
					result.next();
					Product product= new Product();
					product.setId(result.getInt("id"));
					product.setName(result.getString("name"));
					product.setDepartment(result.getString("department"));
					product.setDescription(result.getString("description"));
					product.setImage(result.getString("image"));
					return product;
				}
			}
		}
	}
	
	
	public List<Product> getSuggestedProducts(int num, String department) throws SQLException{
		String query = "SELECT  * FROM product  WHERE department = ? order by rand() limit ?";
		List<Product> list= new ArrayList<>();
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1,department);
			pstatement.setInt(2, num);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // if there are no results, it returns null
					return null;
				
				 while (result.next()){
					Product product= new Product();
					product.setId(result.getInt("id"));
					product.setName(result.getString("name"));
					product.setDepartment(result.getString("department"));
					product.setDescription(result.getString("description"));
					product.setImage(result.getString("image"));
					list.add(product);
				}
				 return list;
			}
		}
	}
	
	
	public Map<Product,Integer> getSearchResults(String search) throws SQLException{
		Map<Product,Integer> map=new HashMap<>();
		String productQuery="SELECT id, name FROM product WHERE name LIKE ?"; 
		String costQuery="SELECT TOP 1 s.price FROM sells AS s JOIN product AS p ON s.product_id = p.id WHERE p.id = ? ORDER BY s.price ASC";
		
		//Find every product corresponding to search input
		List<Product> list= new ArrayList<>();
		try(PreparedStatement pstatement = connection.prepareStatement(productQuery);){
			pstatement.setString(1, "%"+search+"%");
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // If there are no results, it returns null
					return null;
				
				 while (result.next()){
					Product product= new Product();
					product.setId(result.getInt("p.id"));
					product.setName(result.getString("p.name"));
					list.add(product);
				}
			}
		}
		
		//For each product, find minimum price
		try (PreparedStatement pstatement = connection.prepareStatement(costQuery);){
			for(Product p: list) {
				pstatement.setInt(1, p.getId());
				try(ResultSet result = pstatement.executeQuery();){
					if(result.isBeforeFirst()) { //If there's not a minimum price, P isn't added to MAP
						result.next();
						map.put(p, result.getInt("s.price"));
					}
				}		
			}
		}
		return map;
	}
}
