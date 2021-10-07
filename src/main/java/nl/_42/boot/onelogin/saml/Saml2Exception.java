package nl._42.boot.onelogin.saml;

import org.springframework.security.core.AuthenticationException;

public class Saml2Exception extends AuthenticationException {

    public Saml2Exception(String message) {
        super(message);
    }

}
