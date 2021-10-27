package nl._42.boot.onelogin.saml.web;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.settings.Saml2Settings;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class Saml2LoginFilter extends AbstractSaml2Filter {

    private final Saml2Properties properties;

    public Saml2LoginFilter(Saml2Properties properties) {
        super(properties);
        this.properties = properties;
    }

    @Override
    protected void doFilter(Saml2Settings settings, Registration registration, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, SettingsException {
        Auth auth = new Auth(settings, request, response);

        String successUrl = request.getParameter("successUrl");
        if (StringUtils.isNotBlank(successUrl)) {
            HttpSession session = request.getSession();
            session.setAttribute(Saml2SuccessRedirectHandler.SUCCESS_URL_PARAMETER, successUrl);
        }

        String registrationId = getRegistrationId(request);
        String returnTo = properties.getBaseUrl() + "/saml2/SSO/" + registrationId;
        auth.login(returnTo, registration.isForceAuthN(), false, true);
    }

}
