package beans;

public class Product {
	
	private int id;
	private String name;
	private String department;
	private String description;
	private String image;
	
	public Product() {
		
	}
	
	public String getDepartment() {
		return department;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getId() {
		return id;
	}
	
	public String getImage() {
		return image;
	}
	
	public String getName() {
		return name;
	}
	
	public void setDepartment(String department) {
		this.department = department;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
