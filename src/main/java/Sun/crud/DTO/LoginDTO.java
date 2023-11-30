package Sun.crud.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginDTO {
	
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("Access_token")
    private String access_token;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("Refresh_token")
    private String refresh_token;  
    
}