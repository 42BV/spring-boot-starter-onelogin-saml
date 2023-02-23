package nl._42.boot.onelogin.saml.web;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.SAMLException;
import com.onelogin.saml2.settings.Saml2Settings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@Slf4j
public class Saml2LogoutFilter extends AbstractSaml2Filter {

    private final Saml2Properties properties;
    private final Saml2LogoutHandler logoutHandler;

    public Saml2LogoutFilter(Saml2Properties properties, Saml2LogoutHandler logoutHandler) {
        super(properties);
        this.properties = properties;
        this.logoutHandler = logoutHandler;
    }

    @Override
    protected void doFilter(Saml2Settings settings, Registration registration, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, SAMLException {
        if (StringUtils.equalsIgnoreCase(Registration.POST, registration.getLogoutBinding())) {
            String returnTo = properties.getSingleLogoutUrl(registration);
            Auth auth = getAuth(settings, request, response);
            auth.logout(returnTo);
        } else {
            redirect(registration, request, response);
        }
    }

    private void redirect(Registration registration, HttpServletRequest request, HttpServletResponse response) {
        logoutHandler.onLogoutSuccess(registration, request, response);

        String logoutUrl = StringUtils.defaultString(registration.getLogoutUrl(), properties.getSuccessUrl());
        Redirects.redirectTo(response, logoutUrl);
    }

}