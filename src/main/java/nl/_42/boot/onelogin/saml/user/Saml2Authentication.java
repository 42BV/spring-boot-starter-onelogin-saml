package nl._42.boot.onelogin.saml.user;

import lombok.Getter;
import nl._42.boot.onelogin.saml.Saml2Response;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

@Getter
public class Saml2Authentication extends UsernamePasswordAuthenticationToken implements ExpiringAuthenticationToken {

    private final Saml2Response response;
    private final LocalDateTime expiration;

    public Saml2Authentication(UserDetails details, Saml2Response response, LocalDateTime expiration) {
        super(
            details,
            details.getPassword(),
            details.getAuthorities()
        );

        this.response = response;
        this.expiration = expiration;
    }

}
