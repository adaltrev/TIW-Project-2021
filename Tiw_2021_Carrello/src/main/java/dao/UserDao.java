package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.User;


public class UserDao {
	private Connection con;

	public UserDao(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(String username, String password) throws SQLException {
		String query = "SELECT  id, email, name, surname,address FROM user  WHERE email = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // if there are no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("id"));
					user.setEmail(result.getString("email"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					user.setAddress(result.getString("address"));
					return user;
				}
			}
		}
	}
}