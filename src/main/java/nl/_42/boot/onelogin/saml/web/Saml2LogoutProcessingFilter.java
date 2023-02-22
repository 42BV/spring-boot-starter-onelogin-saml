package nl._42.boot.onelogin.saml.web;

import com.onelogin.saml2.settings.Saml2Settings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Properties;

import java.io.IOException;

@Slf4j
public class Saml2LogoutProcessingFilter extends AbstractSaml2Filter {

    private final String successUrl;
    private final Saml2LogoutHandler logoutHandler;

    public Saml2LogoutProcessingFilter(Saml2Properties properties, Saml2LogoutHandler logoutHandler) {
        super(properties);
        this.successUrl = properties.getSuccessUrl();
        this.logoutHandler = logoutHandler;
    }

    @Override
    protected void doFilter(Saml2Settings settings, Registration registration, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {
        logoutHandler.onLogoutSuccess(registration, request, response);
        Redirects.redirectTo(response, successUrl);
    }

}
