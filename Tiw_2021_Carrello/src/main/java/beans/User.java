package beans;

public class User {
	private int id;
	private String email;//used as userName
	private String name;
	private String surname;
	private String address;
	
	public User () {
		
	}
	
	public String getAddress() {
		return address;
	}
	public String getEmail() {
		return email;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getSurname() {
		return surname;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
}
