package platform.codingnomads.co.uiclientapplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @JsonProperty("userId")
    private Long id;

    private String username;

    private String password;

    private String email;

    private String fullName;
}
