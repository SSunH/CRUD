package Sun.crud.DTO;

import lombok.Data;

@Data
public class UserDTO {
	
	private String id;
	private int password;
	private String email;
	private String name;
	private String role;	

}

