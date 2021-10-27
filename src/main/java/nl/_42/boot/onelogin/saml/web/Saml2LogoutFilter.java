package nl._42.boot.onelogin.saml.web;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.SAMLException;
import com.onelogin.saml2.settings.Saml2Settings;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Properties;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class Saml2LogoutFilter extends AbstractSaml2Filter {

    private final Saml2Properties properties;

    public Saml2LogoutFilter(Saml2Properties properties) {
        super(properties);
        this.properties = properties;
    }

    @Override
    protected void doFilter(Saml2Settings settings, Registration registration, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, SAMLException {
        String registrationId = getRegistrationId(request);
        String returnTo = properties.getBaseUrl() + "/saml2/SingleLogout/" + registrationId;

        Auth auth = new Auth(settings, request, response);
        auth.logout(returnTo);
    }

}