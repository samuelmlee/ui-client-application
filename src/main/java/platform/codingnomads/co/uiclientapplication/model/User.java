package platform.codingnomads.co.uiclientapplication.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {

    private Long id;

    private String username;

    private String password;

    private String email;

    private String fullName;

    private List<Role> authorities = new ArrayList<>();
}
