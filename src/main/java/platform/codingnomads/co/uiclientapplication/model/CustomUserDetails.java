package platform.codingnomads.co.uiclientapplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Long id;

    public String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Builder.Default
    private boolean isAccountNonExpired = true;

    @Builder.Default
    private boolean isAccountNonLocked = true;

    @Builder.Default
    private boolean isCredentialsNonExpired = true;

    @Builder.Default
    private boolean isEnabled = true;

    private Collection<GrantedAuthority> authorities = new ArrayList<>();
}
