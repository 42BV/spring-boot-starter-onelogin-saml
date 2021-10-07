package nl._42.boot.onelogin.saml.web;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.SAMLException;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Properties;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class Saml2LogoutFilter extends AbstractSaml2Filter {

    private final String returnTo;

    public Saml2LogoutFilter(Saml2Properties properties, String returnTo) {
        super(properties);
        this.returnTo = returnTo;
    }

    @Override
    protected void doFilter(Auth auth, Registration idp, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, SAMLException {
        auth.logout(returnTo);
    }

}