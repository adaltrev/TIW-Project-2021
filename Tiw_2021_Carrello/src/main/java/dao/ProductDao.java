package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
}
