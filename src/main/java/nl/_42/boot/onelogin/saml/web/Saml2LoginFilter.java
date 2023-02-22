package nl._42.boot.onelogin.saml.web;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.authn.AuthnRequestParams;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.settings.Saml2Settings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import static nl._42.boot.onelogin.saml.web.Saml2SuccessRedirectHandler.SUCCESS_URL_PARAMETER;

@Slf4j
public class Saml2LoginFilter extends AbstractSaml2Filter {

    private final Saml2Properties properties;

    public Saml2LoginFilter(Saml2Properties properties) {
        super(properties);
        this.properties = properties;
    }

    @Override
    protected void doFilter(Saml2Settings settings, Registration registration, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, SettingsException {
        Auth auth = getAuth(settings, request, response);

        String successUrl = request.getParameter(SUCCESS_URL_PARAMETER);
        if (StringUtils.isNotBlank(successUrl)) {
            HttpSession session = request.getSession();
            session.setAttribute(SUCCESS_URL_PARAMETER, successUrl);
        }

        String returnTo = properties.getSignOnUrl(registration);
        auth.login(returnTo, new AuthnRequestParams(registration.isForceAuthN(), false, true));
    }

}
