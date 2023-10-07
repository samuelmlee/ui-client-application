package platform.codingnomads.co.uiclientapplication.model;

import lombok.*;

import javax.validation.constraints.Pattern;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {


    private Long id;

    @Pattern(regexp = "ROLE_USER|ROLE_ADMIN")
    private String role;


}
