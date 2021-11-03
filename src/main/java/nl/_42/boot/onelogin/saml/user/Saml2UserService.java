package nl._42.boot.onelogin.saml.user;

import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Saml2Properties;
import nl._42.boot.onelogin.saml.Saml2Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * SAML implementation of retrieving the user details. Reads the user identifier 
 * from the SAML credentials and then joins this with the person data stored in
 * our system.
 *
 * @author Jeroen van Schagen
 * @since Nov 18, 2014
 */
@Slf4j
public class Saml2UserService {

    private List<Saml2UserDecorator> decorators = new ArrayList<>();

    public Saml2UserService(Saml2Properties properties) {
        Objects.requireNonNull(properties, "Properties are required");
    }

    public UserDetails load(Saml2Response response) throws AuthenticationException {
        UserDetails user = build(response);
        return decorate(user, response);
    }

    private UserDetails build(Saml2Response response) {
        log.debug("Loading user by SAML credentials...");

        String userName = response.getUserName();
        Collection<GrantedAuthority> authorities = response.getAuthorities();

        return new User(userName, "", authorities);
    }

    private UserDetails decorate(UserDetails details, Saml2Response response) {
        for (Saml2UserDecorator decorator : decorators) {
            details = decorator.decorate(details, response);
        }
        return details;
    }

    @Autowired(required = false)
    public void setDecorators(List<Saml2UserDecorator> decorators) {
        this.decorators = decorators;
    }

}
