package nl._42.boot.onelogin.saml.user;

import nl._42.boot.onelogin.saml.Saml2Response;
import org.springframework.security.core.userdetails.UserDetails;

public interface Saml2UserDecorator {

    UserDetails decorate(UserDetails details, Saml2Response response);

}
